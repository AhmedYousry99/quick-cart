package com.senseicoder.quickcart.features.main.ui.shopping_cart

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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.repos.cart.CartRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.BottomSheetPaymentBinding
import com.senseicoder.quickcart.databinding.FragmentShoppingCartBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel.ShoppingCartViewModel
import com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel.ShoppingCartViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class ShoppingCartFragment : Fragment(), OnCartItemClickListner {
    companion object {
        private const val TAG = "ShoppingCartFragment"
        private const val HARD_CODED_CARD_ID =
            "gid://shopify/Cart/Z2NwLWV1cm9wZS13ZXN0MTowMUo5TUoxMVdDOVJHTjlQVjdWRzZCRUo3Vw?key=6d25762dfc534d13b171a91f490f8804"
    }

    private lateinit var fragmentBinding: FragmentShoppingCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var sharedViewModel: MainActivityViewModel
    lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBinding: BottomSheetPaymentBinding
    private val viewModel: ShoppingCartViewModel by lazy {
        ViewModelProvider(
            this,
            ShoppingCartViewModelFactory(
                CartRepoImpl(
                    StorefrontHandlerImpl,
                    SharedPrefsService
                )
            )
        )[ShoppingCartViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentBinding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        // TODO: get from view model
        viewModel.fetchCartProducts(SharedPrefsService.getSharedPrefString(Constants.CART_ID, Constants.CART_ID_DEFAULT))
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
                            val fetchedList: List<ProductOfCart>? = it.data
                            adapter = CartAdapter(this@ShoppingCartFragment)
                            adapter.submitList(fetchedList)
//                            Log.d(TAG, "onViewCreated: ${fetchedList[0].productTitle}")
                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            if (!fetchedList.isNullOrEmpty()) {
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

    fun updateTotalPrice(list: List<ProductOfCart>?) {
        val res = Math.round(
            (list?.sumOf { it.variantPrice!!.toDouble() * it.quantity })?.times(100.0) ?: 0.00
        ) / 100.0
        fragmentBinding.txtValueOfGrandTotal.text = String.format(res.toString())
    }

    override fun onProductClick(item: ProductOfCart) {
        if(NetworkUtils.isConnected(requireContext())){
            sharedViewModel.setCurrentProductId(item.productId!!.split("/").last().apply {
                Log.d(TAG, "onProductClick: $this")
            })
            Navigation.findNavController(this.requireView())
                .navigate(R.id.action_shoppingCartFragment_to_productDetailsFragment)
        }else{
            showSnackbar(getString(R.string.no_internet_connection))
        }
    }

    override fun onPlusClick(item: ProductOfCart) {
        fragmentBinding.apply {
            val old = txtValueOfGrandTotal.text.toString().toDouble()
            val new = old + item.variantPrice!!.toDouble()
            txtValueOfGrandTotal.text = String.format(new.toString())
            Log.d("Filo", "onPlusClick: ${item.quantity}")
            viewModel.updateQuantityOfProduct(HARD_CODED_CARD_ID, item.linesId!!,item.quantity)
        }
    }

    override fun onMinusClick(item: ProductOfCart) {
        fragmentBinding.apply {
            val old = txtValueOfGrandTotal.text.toString().toDouble()
            val new = old - item.variantPrice!!.toDouble()
            txtValueOfGrandTotal.text = String.format(new.toString())
            viewModel.updateQuantityOfProduct(HARD_CODED_CARD_ID, item.linesId!!,item.quantity)
        }
    }

    override fun onDeleteClick(item: ProductOfCart) {
        ConfirmationDialogFragment(DialogType.DEL_PRODUCT) {
            viewModel.deleteFromCart(HARD_CODED_CARD_ID, item.id)
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
                            viewModel.refresh(HARD_CODED_CARD_ID)
                        }

                        is ApiState.Failure -> {
                        }
                    }
                }
            }
        }.show(childFragmentManager, "ConfirmationDialogFragment")
    }

    fun showBottomSheet(){
        bottomSheetBinding = BottomSheetPaymentBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)  // Use binding.root instead of dialogView
        bottomSheetDialog.show()
        functionalityWhenBtnCompleteParchesClicked(bottomSheetBinding)
        setPrices(bottomSheetBinding)
    }
    private fun setPrices(binding: BottomSheetPaymentBinding){
        binding.apply {
            txtValueOfBeforeDiscount.text = fragmentBinding.txtValueOfGrandTotal.text
            txtValueOfDiscount.text = String.format(0.0.toString())
            txtValueOFAfterDiscount.text = fragmentBinding.txtValueOfGrandTotal.text
            btnAddCoupon.setOnClickListener{
                //TODO HERE CHECK IF VALID COUPON
                if (false){
                    Snackbar.make(this.root.rootView, "Valid Coupon", Toast.LENGTH_SHORT).show()
                    //TODO ADD DISCOUNT TO PRICE
                    //TODO AND TOTAL AFTER DISCOUNT UPDATE
                }else
                    Snackbar.make(this.root.rootView, "Invalid Coupon", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun functionalityWhenBtnCompleteParchesClicked(binding: BottomSheetPaymentBinding){
        binding.apply {
            btnCompletePurches.setOnClickListener {
                if(radBtnCash.isChecked){
                    Toast.makeText(requireContext(), "Cash", Toast.LENGTH_SHORT).show()
                }else if(radBtnCard.isChecked){
                    Toast.makeText(requireContext(), "Card", Toast.LENGTH_SHORT).show()
                }else
                    Toast.makeText(requireContext(), "Please select payment method", Toast.LENGTH_SHORT).show()
            }
        }
    }
}