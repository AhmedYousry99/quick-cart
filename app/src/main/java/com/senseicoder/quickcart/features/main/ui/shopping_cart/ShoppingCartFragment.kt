package com.senseicoder.quickcart.features.main.ui.shopping_cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.dialogs.PaymentProcessesDialog
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.global.trimCurrencySymbol
import com.senseicoder.quickcart.core.global.updateCurrency
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.Customer
import com.senseicoder.quickcart.core.model.DiscountCodesDTO
import com.senseicoder.quickcart.core.model.DraftOrder
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import com.senseicoder.quickcart.core.model.PriceRule
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.model.noteAttributesData
import com.senseicoder.quickcart.core.model.toAddress
import com.senseicoder.quickcart.core.model.toApplied_Discount
import com.senseicoder.quickcart.core.model.toDiscountCodeDto
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
import com.senseicoder.quickcart.databinding.PaymentProccessDialogBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModelFactory
import com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel.ShoppingCartViewModel
import com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel.ShoppingCartViewModelFactory
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.Locale

class ShoppingCartFragment : Fragment(), OnCartItemClickListener {

    companion object {
        private const val TAG = "ShoppingCartFragment"
    }

    lateinit var customerId: String
    lateinit var customerEphemeralKey: String
    private lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfiguration: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String
    private val customScope = CoroutineScope(Dispatchers.Main)
    lateinit var draftOrder: DraftOrder
    private lateinit var fragmentBinding: FragmentShoppingCartBinding
    private lateinit var fetchedList: List<ProductOfCart>
    lateinit var couponsList: List<PriceRule>
    private lateinit var adapter: CartAdapter
    lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBinding: BottomSheetPaymentBinding
    private var defaultAddress: AddressOfCustomer? = null
    private lateinit var currencyData: Triple<String?, String?, Float?>
    private val sharedViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
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
    lateinit var couponToUse: DiscountCodesDTO
    private val cardId =
        SharedPrefsService.getSharedPrefString(Constants.CART_ID, Constants.CART_ID_DEFAULT)
    private var paymentProcess = PaymentProcessesDialog(this)

    fun collectCouponsList() {
        lifecycleScope.launch {
            viewModel.couponDetails.first {
                when (it) {
                    is ApiState.Success -> {
                        couponsList = it.data.price_rules
                        Log.d(TAG, "collectCouponsList: ${it.data}")
                        true
                    }

                    else -> {
                        Log.d(TAG, "collectCouponsList: ${it}")
                        false
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectCouponsList()
        paymentSheet =
            PaymentSheet(this@ShoppingCartFragment, this@ShoppingCartFragment::onPaymentSheetResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        SharedPrefsService.logAllSharedPref(TAG, "ONCREATE")
        fragmentBinding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchCoupons()
        collectDefaultAddress()
        createOrderCollector()
        completeDraftOrderForCashCollector()
        currencyData = SharedPrefsService.getCurrencyData()
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
                            shimmerFrameLayout.visibility = View.GONE
                            shimmerFrameLayout.stopShimmer()
                            Snackbar.make(requireView(), it.msg, Snackbar.LENGTH_SHORT).show()
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
                        Log.d(TAG, "onViewCreated: ${it.data.toString()}")

                    }

                    is ApiState.Failure -> {
                        Log.d(TAG, "onViewCreated: ${it.msg}")
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
        customScope.cancel()
        (requireActivity() as MainActivity).apply {
            if (findNavController().currentDestination!!.id == R.id.homeFragment
                || findNavController().currentDestination!!.id == R.id.favoriteFragment
                || findNavController().currentDestination!!.id == R.id.shoppingCartFragment
                || findNavController().currentDestination!!.id == R.id.profileFragment
            ) {
                showBottomNavBar()
            } else {
                hideBottomNavBar()
            }
        }
    }

    private fun completeDraftOrderForCashCollector() {
        customScope.launch {
            viewModel.apply {
                draftOrderCompletion.collect {
                    when (it) {
                        is ApiState.Success -> {
                            customScope.launch {
                                freeCart()
                            }.join()
                            bottomSheetDialog.dismiss()
                            Log.d(
                                TAG,
                                "completeDraftOrderForCash: success id : ${it.data.draft_order.id}"
                            )
                            paymentProcess.stepThree()
                        }

                        is ApiState.Loading -> Log.d(TAG, "completeDraftOrderForCash: loading ")

                        else -> {
                            paymentProcess.errorOccur(2)
                            Log.d(TAG, "completeDraftOrderForCash: error  ${it}")
                        }

                    }
                }
            }

        }
    }

    private fun createOrderCollector() {
        customScope.launch {
            viewModel.apply {
                draftOrderCreation.collect {
                    when (it) {
                        is ApiState.Success -> {
                            Log.d(
                                TAG,
                                "createDraftOrderForCash Success: ${it.data}"
                            )
                            delay(1000)
                            paymentProcess.stepTwo()
                            viewModel.completeDraftOrder(it.data.draft_order.id)
                        }

                        is ApiState.Failure -> {
                            Log.d(TAG, "createDraftOrderForCash: failure ${it.msg}")
                            paymentProcess.errorOccur(1)
                        }

                        else -> Log.d(TAG, "createDraftOrderForCash loading:")
                    }
                }
            }
        }
    }

    private fun createDraftOrder(type : String) {
        customScope.launch {
            val lines = fetchedList.map {
                it.toLineItem()
            }
            val email = SharedPrefsService.getSharedPrefString(
                Constants.USER_EMAIL,
                Constants.USER_EMAIL_DEFAULT
            )
            if (defaultAddress != null) {
                if (bottomSheetBinding.txtValueOfDiscount.text.toString().trimCurrencySymbol()
                        .replace("-", "").toDouble() > 0.0
                ) {
                    draftOrder = DraftOrder(
                        email,
                        LocalDateTime.now().nano.toLong(),
                        lines,
                        Customer(email),
                        defaultAddress!!.toAddress(),
                        defaultAddress!!.toAddress(),
                        couponToUse.toApplied_Discount(),
                        listOf(noteAttributesData(Constants.PAYMENT_TYPE, type))
                    )
                } else
                    draftOrder = DraftOrder(
                        email,
                        LocalDateTime.now().nano.toLong(),
                        lines,
                        Customer(email),
                        defaultAddress!!.toAddress(),
                        defaultAddress!!.toAddress(),
                        note_attributes = listOf(noteAttributesData(Constants.PAYMENT_TYPE, type)
                    ))

                val request = DraftOrderReqRes(draftOrder)
                viewModel.createDraftOrder(request)
            } else
                Log.d(TAG, "createDraftOrderForCash: default add not found $defaultAddress")
        }
    }


    /* private fun sendInvoiceCollector(id: Long) {
         customScope.launch {
             customScope.launch { viewModel.sendInvoice(id) }.join()
             viewModel.sendInvoice.collect {
                 when (it) {
                     is ApiState.Success -> {
                         Log.d(
                             TAG,
                             "sendInvoiceCollector success invoice ${it.data?.draft_order}: "
                         )
                     }

                     is ApiState.Loading -> {
                         Log.d(TAG, "sendInvoiceCollector  inloading : ")
                     }

                     else -> Log.d(TAG, "sendInvoiceCollector: error ${it}")
                 }
             }
         }
     }*/

    private fun updateTotalPrice(list: List<ProductOfCart>?) {
        val res = Math.round(
            (list?.sumOf { it.variantPrice!!.toDouble() * it.quantity })?.times(100.0) ?: 0.00
        ) / 100.0
        fragmentBinding.txtValueOfGrandTotal.text = String.format(
            "${
                res.toString().updateCurrency(currencyData.third)
            } ${currencyData.second}"
        )
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
        if (defaultAddress == null) {
            bottomSheetBinding.apply {
                btnCompletePurches.isEnabled = false
                btnAddAddress.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        Navigation.findNavController(this@ShoppingCartFragment.requireView())
                            .navigate(
                                R.id.action_shoppingCartFragment_to_addressFragment,
                                bundleOf(Constants.LABEL to Constants.CART_FRAGMENT_TO_ADD)
                            )
                        bottomSheetDialog.dismiss()
                    }
                }
            }
        } else {
            bottomSheetBinding.apply {
                groupAddressData.visibility = View.VISIBLE
                btnAddAddress.visibility = View.GONE
                txtValueOfAddress.text =
                    String.format("${defaultAddress?.firstName} ${defaultAddress?.lastName}\n${defaultAddress?.country} ${defaultAddress?.city}")
                imgEditDefaultAddress.setOnClickListener {
                    Navigation.findNavController(this@ShoppingCartFragment.requireView()).navigate(
                        R.id.action_shoppingCartFragment_to_addressFragment,
                        bundleOf(Constants.LABEL to Constants.CART_FRAGMENT_TO_EDIT)
                    )
                    bottomSheetDialog.dismiss()
                }
            }
        }
    }


    @SuppressLint("DefaultLocale")
    private fun setDiscount() {
        bottomSheetBinding.apply {
            Log.d(TAG, "setDiscount: ${couponToUse}")
            val oldAfter = txtValueOfBeforeDiscount.text.toString().trimCurrencySymbol().toDouble()
            Log.d(TAG, "setDiscount: $oldAfter")
            val percentage = (couponToUse.value.replace("-", "")).toDouble()
            Log.d(TAG, "setDiscount: $percentage")
            val newAfter = (oldAfter - (oldAfter * (percentage / 100)))
            Log.d(TAG, "setDiscount: $newAfter")
            "${
                String.format(
                    "%.2f",
                    newAfter
                )
            } ${currencyData.second}".also { txtValueOFAfterDiscount.text = it }
        }
    }

    private fun setPrices(binding: BottomSheetPaymentBinding) {
        binding.apply {
            txtValueOfBeforeDiscount.text = fragmentBinding.txtValueOfGrandTotal.text
            txtValueOfDiscount.text = String.format(0.0.toString())
            txtValueOFAfterDiscount.text = fragmentBinding.txtValueOfGrandTotal.text
            btnAddCoupon.setOnClickListener {
                val userInputCoupons = editTxtCoupon.text.trim()
                val isCouponValid = couponsList.any { it.title == userInputCoupons.toString() }
                if (isCouponValid) {
                    Toast.makeText(requireContext(), "Coupon is valid", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "setPrices: ${userInputCoupons}")
                    couponToUse = couponsList.first { it.title == userInputCoupons.toString() }
                        .toDiscountCodeDto()
                    editTxtCoupon.text.clear()
                    "${this@ShoppingCartFragment.couponToUse.value} %".also {
                        txtValueOfDiscount.text = it
                    }
                    setDiscount()
                } else {
                    Toast.makeText(requireContext(), "Coupon is not valid", Toast.LENGTH_SHORT)
                        .show()
                    txtValueOfDiscount.text = String.format(0.0.toString())
                    txtValueOFAfterDiscount.text = fragmentBinding.txtValueOfGrandTotal.text
                }
            }
        }
    }


    private fun functionalityWhenBtnCompleteParchesClicked(binding: BottomSheetPaymentBinding) {
        binding.apply {
            btnCompletePurches.setOnClickListener {
                if (radBtnCash.isChecked) {
                    lifecycleScope.launch { paymentProcess.show(childFragmentManager, null) }
                        .invokeOnCompletion {
                            createDraftOrder(Constants.CASH)
                        }
                } else if (radBtnCard.isChecked) {
                        checkOut()
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


    @SuppressLint("DefaultLocale")
    override fun onPlusClick(item: ProductOfCart) {
        fragmentBinding.apply {
            val old = txtValueOfGrandTotal.text.toString().trimCurrencySymbol().toDouble()
            val new = old + item.variantPrice?.updateCurrency(currencyData.third)!!
            "${
                String.format(
                    " % .2f",
                    new
                )
            } ${currencyData.second}".also { txtValueOfGrandTotal.text = it }
            Log.d("Filo", "onPlusClick: ${item.quantity}")
            viewModel.updateQuantityOfProduct(cardId, item.linesId!!, item.quantity)
        }
    }


    @SuppressLint("DefaultLocale")
    override fun onMinusClick(item: ProductOfCart) {
        fragmentBinding.apply {
            val old = txtValueOfGrandTotal.text.toString().trimCurrencySymbol().toDouble()
            val new = old - item.variantPrice?.updateCurrency(currencyData.third)!!
            "${
                String.format(
                    " % .2f",
                    new
                )
            } ${currencyData.second}".also { txtValueOfGrandTotal.text = it }
            viewModel.updateQuantityOfProduct(cardId, item.linesId!!, item.quantity)
        }
    }

    override fun onDeleteClick(item: ProductOfCart) {
        ConfirmationDialogFragment(DialogType.DEL_PRODUCT) {
            viewModel.deleteFromCart(cardId, item.id)
            lifecycleScope.launch {
                viewModel.removeProductFromCart.collect {
                    when (it) {
                        is ApiState.Success -> {
                            Snackbar.make(
                                fragmentBinding.root,
                                "Product deleted successfully",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                        }
                    }
                }
            }
        }.show(childFragmentManager, "ConfirmationDialogFragment")
    }

    override fun paymentProcessDialog(binding: PaymentProccessDialogBinding) {
        TODO("Not yet implemented")
    }
    /*

        fun startPaymentSheet() {
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                PaymentSheet.Configuration(
                    "Quick Cart", customerConfiguration
                )
            )
        }


        fun hitApiAndFetchData() {
            val queue = Volley.newRequestQueue(requireContext())
            val url =
                "http://100.42.177.115:8888/create-payment-intent"

            val stringRequest: StringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    // response
                    val jsonObj = JSONObject(response)
                    customerConfiguration = PaymentSheet.CustomerConfiguration(
                        jsonObj.getString("customer"),
                        jsonObj.getString("ephemeralKey"),
                    )
                    paymentIntentClientSecret = jsonObj.getString("paymentIntent")
                    PaymentConfiguration.init(
                        this.requireActivity().applicationContext,
                        jsonObj.getString("publishableKey")
                    )
                    Log.d(TAG, "hitApiAndFetchData:respone  ${response}")
                }, Response.ErrorListener { it ->
                    Log.d(TAG, "hitApiAndFetchData: error ${it}")
                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["authKey"] = "abc"
                    return params
                }
            }
            queue.add(stringRequest)
        }
    */

    private fun checkOut() {
        val reqBody = JSONObject().apply {
            put(
                "amount",
                bottomSheetBinding.txtValueOFAfterDiscount.text.toString().trimCurrencySymbol()
                    .toDoubleOrNull()?.times(100) ?: 100
            )
            put(
                "currency",
                SharedPrefsService.getSharedPrefString(
                    Constants.CURRENCY,
                    Constants.CURRENCY_DEFAULT
                )
                    .lowercase(Locale.getDefault())
            )
            put(
                "email",
                SharedPrefsService.getSharedPrefString(
                    Constants.USER_EMAIL,
                    Constants.USER_EMAIL_DEFAULT
                )
            )
            put(
                "name",
                SharedPrefsService.getSharedPrefString(
                    Constants.USER_DISPLAY_NAME,
                    Constants.USER_DISPLAY_NAME_DEFAULT
                )
            )
        }
        Log.d(TAG, "checkOut: $reqBody")
        val url = "http://100.42.177.115:8888/create-payment-intent"
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, reqBody,
            { response ->
                paymentIntentClientSecret = response.getString("clientSecret")
                customerEphemeralKey = response.getString("ephemeralKey")
                customerId = response.getString("customer")
                presentPaymentSheet()
                Log.d(TAG, "checkOut:(success) $response")
            },
            { error ->
                Log.d(TAG, "checkOut(error): ${error}")
            }
        )
        Volley.newRequestQueue(requireContext()).add(jsonRequest)
    }

    private fun presentPaymentSheet() {
        val paymentSheetConfiguration = PaymentSheet.Configuration(
            getString(R.string.app_name),
            PaymentSheet.CustomerConfiguration(
                customerId, customerEphemeralKey
            )
        )
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            paymentSheetConfiguration
        )
    }

    private fun onPaymentSheetResult(res: PaymentSheetResult) {
        when (res) {
            is PaymentSheetResult.Canceled -> Log.d(TAG, "onPaymentSheetResult: canceled")
            is PaymentSheetResult.Failed -> Log.d(TAG, "onPaymentSheetResult: failed")
            is PaymentSheetResult.Completed -> {
                createDraftOrder(Constants.CARD)
                Log.d(TAG, "onPaymentSheetResult: completed")
            }
        }

    }
}

