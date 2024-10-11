package com.senseicoder.quickcart.features.main.ui.shopping_cart

import android.icu.util.LocaleData
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.dialogs.PaymentDialog
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.Customer
import com.senseicoder.quickcart.core.model.DraftOrder
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.model.toAddress
import com.senseicoder.quickcart.core.model.toAddressOfCustomer
import com.senseicoder.quickcart.core.model.toLineItem
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.coupons.CouponsRemoteImpl
import com.senseicoder.quickcart.core.network.currency.CurrencyRemoteImpl
import com.senseicoder.quickcart.core.network.order.OrderRemoteDataSourceImpl
import com.senseicoder.quickcart.core.repos.cart.CartRepoImpl
import com.senseicoder.quickcart.core.repos.coupons.CouponsRepoImpl
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepoImpl
import com.senseicoder.quickcart.core.repos.order.draft_order.DraftOrderRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.BottomSheetPaymentBinding
import com.senseicoder.quickcart.databinding.FragmentShoppingCartBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModelFactory
import com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel.ShoppingCartViewModel
import com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel.ShoppingCartViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale
import kotlin.math.log

class ShoppingCartFragment : Fragment(), OnCartItemClickListner {

    companion object {
        private const val TAG = "ShoppingCartFragment"
    }

    private val customScope = CoroutineScope(Dispatchers.Main)
    private lateinit var fragmentBinding: FragmentShoppingCartBinding
    private lateinit var fetchedList: List<ProductOfCart>
    private lateinit var adapter: CartAdapter
    lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBinding: BottomSheetPaymentBinding
    private var defaultAddress: AddressOfCustomer? = null
    private val sharedViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(requireActivity(),
            MainActivityViewModelFactory(
                CurrencyRepoImpl(
                    CurrencyRemoteImpl
                )
            )
            )[MainActivityViewModel::class.java]
    }
    private val viewModel: ShoppingCartViewModel by lazy {
        ViewModelProvider(
            this,
            ShoppingCartViewModelFactory(
                CartRepoImpl(
                    StorefrontHandlerImpl,
                    SharedPrefsService
                ),
                DraftOrderRepoImpl(OrderRemoteDataSourceImpl),
                CouponsRepoImpl(CouponsRemoteImpl())
            )
        )[ShoppingCartViewModel::class.java]
    }
    private val cardId =
        SharedPrefsService.getSharedPrefString(Constants.CART_ID, Constants.CART_ID_DEFAULT)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SharedPrefsService.logAllSharedPref(TAG,"ONCREATE")
        fragmentBinding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        PaymentDialog().show(childFragmentManager, "PaymentDialog")
        collectDefaultAddress()
        // TODO: get from view model
        viewModel.fetchCartProducts(
            SharedPrefsService.getSharedPrefString(
                Constants.CART_ID,
                Constants.CART_ID_DEFAULT
            )
        )
        fragmentBinding.apply {
            btnToPayment.setOnClickListener {
                showBottomSheet()
            }
            lifecycleScope.launch {
                viewModel.cartProducts.collect {
                    when (it) {
                        is ApiState.Loading -> {
                            shimmerFrameLayout.visibility = View.VISIBLE
                            shimmerFrameLayout.startShimmer()
                            animationView.visibility = View.GONE
                            dataGroup.visibility = View.GONE
                        }

                        is ApiState.Success -> {
                            fetchedList = it.data ?: emptyList()
                            adapter = CartAdapter(this@ShoppingCartFragment)
                            adapter.submitList(fetchedList)
//                            Log.d(TAG, "onViewCreated: ${fetchedList[0].productTitle}")
                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            if (fetchedList.isNotEmpty()) {
                                Log.d(TAG, "onViewCreated: ${fetchedList.get(0).linesId ?: ""}   ")
                                updateTotalPrice(fetchedList)
                                dataGroup.visibility = View.VISIBLE
                                animationView.visibility = View.GONE
                                rvDraftOrder.adapter = adapter
                            } else {
                                animationView.visibility = View.VISIBLE
                                dataGroup.visibility = View.GONE
                            }
                        }

                        is ApiState.Failure -> {
                            // TODO HANDLE OTHER ANIMATION FOR ERROR
                            animationView.visibility = View.VISIBLE
                            dataGroup.visibility = View.GONE
                        }

                        is ApiState.Init -> {
                        }
                    }
                }
            }
            Log.d(
                TAG,
                "onViewCreated: ${
                    SharedPrefsService.getSharedPrefString(
                        Constants.USER_TOKEN,
                        Constants.USER_TOKEN_DEFAULT
                    )
                }"
            )
        }
        lifecycleScope.launch {
            viewModel.updating.debounce(500).collect {
                when (it) {
                    is ApiState.Success -> {
                        Snackbar.make(
                            fragmentBinding.root,
                            "Product updated successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is ApiState.Failure -> {
                        Snackbar.make(
                            fragmentBinding.root,
                            "Something went wrong",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).apply {
            toolbarVisibility(false)
            showBottomNavBar()
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).apply {
            if (findNavController().currentDestination!!.id == R.id.homeFragment
                || findNavController().currentDestination!!.id == R.id.favoriteFragment
                || findNavController().currentDestination!!.id == R.id.shoppingCartFragment
                || findNavController().currentDestination!!.id == R.id.profileFragment
            ){
                showBottomNavBar()
            }else{
                hideBottomNavBar()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        customScope.cancel()
    }

    private fun createDraftOrderForCash() {
        customScope.launch {
            val lines = fetchedList.map {
                it.toLineItem()
            }
            val email = SharedPrefsService.getSharedPrefString(
                Constants.USER_EMAIL,
                Constants.USER_EMAIL_DEFAULT
            )
            if (defaultAddress != null) {
                val draft = DraftOrder(
                    email,
                    LocalDateTime.now().nano.toLong(),
                    lines,
                    Customer(email),
                    defaultAddress!!.toAddress(),
                    defaultAddress!!.toAddress()
                )
                val request = DraftOrderReqRes(draft)
                viewModel.apply {
                    createDraftOrder(request)
                    draftOrderCreation.collect {
                        when (it) {
                            is ApiState.Success -> {
                                Log.d(
                                    TAG,
                                    "createDraftOrderForCash Success: ${it.data}"
                                )
                                completeDraftOrderForCash(it.data.draft_order.id)
                            }
                            is ApiState.Failure -> {
                                Log.d(TAG, "createDraftOrderForCash: failure ${it.msg}")}

                            else -> Log.d(TAG, "createDraftOrderForCash loading:")
                        }
                    }
                }
            } else
                Log.d(TAG, "createDraftOrderForCash: default add not found $defaultAddress")

        }
    }

    private fun completeDraftOrderForCash(id: Long) {
        customScope.launch {
            viewModel.apply {
                customScope.launch{ completeDraftOrder(id) }.join()
                draftOrderCompletion.collect {
                    when (it) {
                        is ApiState.Success -> {
                            customScope.launch {
                                freeCart()
                            }.join()
                            bottomSheetDialog.dismiss()
                            Log.d(TAG, "completeDraftOrderForCash: success id : ${it.data.draft_order.id}")
                            customScope.launch { sendInvoice(it.data.draft_order.id) }.join()
                            sendInvoice.collect {
                                when (it) {
                                    is ApiState.Success -> {
                                        Log.d(TAG, "completeDraftOrderForCash success ${it.data.draft_order}: ")
                                    }

                                    is ApiState.Loading -> {
                                        Log.d(TAG, "completeDraftOrderForCash loading : ")
                                    }
                                    else -> Log.d(TAG, "completeDraftOrderForCash: error ${it}")
                                }
                            }

                        }
                        is ApiState.Loading -> Log.d(TAG, "completeDraftOrderForCash: loading ")

                        else -> {
                            Log.d(TAG, "completeDraftOrderForCash: error  ${it}")
                        }

                    }
                }
            }

        }
    }


    private fun updateTotalPrice(list: List<ProductOfCart>?) {
        val res = Math.round(
            (list?.sumOf { it.variantPrice!!.toDouble() * it.quantity })?.times(100.0) ?: 0.00
        ) / 100.0
        fragmentBinding.txtValueOfGrandTotal.text = String.format(res.toString())
    }

    private suspend fun freeCart() {
        for (product in fetchedList) {
            val removeJob = lifecycleScope.launch {
                viewModel.deleteFromCart(cardId, product.id)
            }
            removeJob.join()

            viewModel.removeProductFromCart.first { removeResult ->
                when (removeResult) {
                    is ApiState.Success -> {
                        Log.i(TAG, "Successfully removed item: ${product.id}")
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
        Snackbar.make(requireView(), "Order Completed", Toast.LENGTH_LONG).show()
    }


    private fun showBottomSheet() {
        bottomSheetBinding = BottomSheetPaymentBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)  // Use binding.root instead of dialogView
        bottomSheetDialog.show()
        functionalityWhenBtnCompleteParchesClicked(bottomSheetBinding)
        setPrices(bottomSheetBinding)
    }

    private fun setPrices(binding: BottomSheetPaymentBinding) {
        binding.apply {
            txtValueOfBeforeDiscount.text = fragmentBinding.txtValueOfGrandTotal.text
            txtValueOfDiscount.text = String.format(0.0.toString())
            txtValueOFAfterDiscount.text = fragmentBinding.txtValueOfGrandTotal.text
            btnAddCoupon.setOnClickListener {
                //TODO HERE CHECK IF VALID COUPON
                if (false) {
                    Snackbar.make(this.root.rootView, "Valid Coupon", Toast.LENGTH_SHORT).show()
                    //TODO ADD DISCOUNT TO PRICE
                    //TODO AND TOTAL AFTER DISCOUNT UPDATE
                } else
                    Snackbar.make(this.root.rootView, "Invalid Coupon", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun functionalityWhenBtnCompleteParchesClicked(binding: BottomSheetPaymentBinding) {
        binding.apply {
            btnCompletePurches.setOnClickListener {
                if (radBtnCash.isChecked) {
                    Toast.makeText(requireContext(), "Cash", Toast.LENGTH_SHORT).show()
                    createDraftOrderForCash()
                } else if (radBtnCard.isChecked) {
                    Toast.makeText(requireContext(), "Card", Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(
                        requireContext(),
                        "Please select payment method",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun collectDefaultAddress() {
        lifecycleScope.launch(Dispatchers.Main) {
            lifecycleScope.launch { viewModel.getAddress() }.join()
            viewModel.defaultAddress.first() {
                when (it) {
                    is ApiState.Success -> {
                        defaultAddress = it.data
                        Log.d(TAG, "collectDefaultAddress: DEFAULT_ADDRESS ${it.data}")
                        true
                    }

                    else -> {
                        defaultAddress = null
                        Log.d(TAG, "collectDefaultAddress: ${it}")
                        false
                    }
                }
            }
        }
    }

    // HANDEL CLICK ON PRODUCT IN CART
    override fun onProductClick(item: ProductOfCart) {
        if (NetworkUtils.isConnected(requireContext())) {
            sharedViewModel.setCurrentProductId(item.productId!!.split("/").last().apply {
                Log.d(TAG, "onProductClick: $this")
            })
            Navigation.findNavController(this.requireView())
                .navigate(R.id.action_shoppingCartFragment_to_productDetailsFragment)
        } else {
            showSnackbar(getString(R.string.no_internet_connection))
        }
    }


    override fun onPlusClick(item: ProductOfCart) {
        fragmentBinding.apply {
            val old = txtValueOfGrandTotal.text.toString().toDouble()
            val new = old + item.variantPrice!!.toDouble()
            txtValueOfGrandTotal.text = String.format(new.toString())
            Log.d("Filo", "onPlusClick: ${item.quantity}")
            viewModel.updateQuantityOfProduct(cardId, item.linesId!!, item.quantity)
        }
    }


    override fun onMinusClick(item: ProductOfCart) {
        fragmentBinding.apply {
            val old = txtValueOfGrandTotal.text.toString().toDouble()
            val new = old - item.variantPrice!!.toDouble()
            txtValueOfGrandTotal.text = String.format(new.toString())
            viewModel.updateQuantityOfProduct(cardId, item.linesId!!, item.quantity)
        }
    }

    override fun onDeleteClick(item: ProductOfCart) {
        ConfirmationDialogFragment(DialogType.DEL_PRODUCT) {
            viewModel.deleteFromCart(cardId, item.id)
            lifecycleScope.launch {
                viewModel.removeProductFromCart.collect {
                    when (it) {
                        is ApiState.Loading -> {
                        }

                        is ApiState.Init -> {

                        }

                        is ApiState.Success -> {
//                            viewModel.fetchCartProducts(HARD_CODED_CARD_ID)
                            Snackbar.make(
                                fragmentBinding.root,
                                "Product deleted successfully",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            viewModel.refresh(cardId)
                        }

                        is ApiState.Failure -> {
                        }
                    }
                }
            }
        }.show(childFragmentManager, "ConfirmationDialogFragment")
    }

}