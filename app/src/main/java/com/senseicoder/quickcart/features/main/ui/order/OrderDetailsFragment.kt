package com.senseicoder.quickcart.features.main.ui.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.toTwoDecimalPlaces
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.repos.order.OrderRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentOrderDetailsBinding
import com.senseicoder.quickcart.features.main.ui.brand.BrandAdapter
import com.senseicoder.quickcart.features.main.ui.order.viewmodel.OrderViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var productsManager: LinearLayoutManager
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var viewModel: OrderViewModel


    companion object {
        private const val TAG = "OrderDetailsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpProductsAdapter()
        collectOrderDetails()
    }


    private fun setUpViewModel() {

        val storefrontHandler = StorefrontHandlerImpl
        val sharedPreferences = SharedPrefsService
        val factory = OrderViewModel.OrdersViewModelFactory(
            OrderRepoImpl( storefrontHandler ,
                sharedPreferences)
        )

        viewModel = ViewModelProvider(requireActivity(), factory).get(OrderViewModel::class.java)
    }

    private fun setUpProductsAdapter() {
        productsAdapter = ProductsAdapter(requireContext()) { productId ->

        }
        productsManager = LinearLayoutManager(requireContext())
        productsManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.orderProductsRv.layoutManager = productsManager
        binding.orderProductsRv.adapter = productsAdapter


        // Update currency in the adapter whenever it's set up
        productsAdapter.updateCurrency()

    }

    @SuppressLint("SetTextI18n")
    private fun collectOrderDetails() {
        val index = arguments?.getInt("index") ?: 0
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apiState.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {

                        }

                        is ApiState.Success -> {

//                            binding.totalPrice.text = result.data[index].totalPriceAmount+"EGP"
//                            binding.subTotal.text = result.data[index].subTotalPriceAmount+"EGP"
//                            binding.tax.text = result.data[index].totalTaxAmount+"EGP"

                            // Use the formatPrice function for conversion
                            Log.d(TAG, "collectOrderDetails: $index")
                            val total =
                                result.data[index].totalPriceAmount.toDoubleOrNull()!! - (result.data[index].totalPriceAmount.toDoubleOrNull()!! * (result.data[index].percentage ?: 0.0f)/100.0)
                            Log.d(TAG, "collectOrderDetails: ${result}")
                            binding.totalPrice.text = formatPrice(result.data[index].totalPriceAmount.toDouble())
                            binding.subTotal.text = formatPrice( result.data[index].subTotalPriceAmount.toDouble())
                            binding.tax.text = "${( result.data[index].totalPriceAmount.toFloat()/ (result.data[index].products.fold(0.0){
                                    acc, product ->
                                acc + (product.price.toDoubleOrNull() ?: 0.0)
                            }) * 100).toTwoDecimalPlaces()} %"

                            binding.orderId.text = result.data[index].name
                            binding.itemsCount.text =
                                "${result.data[index].products.count()} item"

//                            binding.address.text =
//                                result.data[index].address?: "Maimi,Alexandria,Egypt"

                            // Set address, concatenating street name, city, and country
                            val address = result.data[index].address
                          //  val fullAddress = listOfNotNull(address?.address1, address?.city, address?.country).joinToString(", ")

                            val street = address?.address2 ?: "Unknown Street"
                            val city = address?.city ?: "Unknown City"
                            val country = address?.country ?: "Unknown Country"

                            val fullAddress = listOfNotNull(street, city, country).joinToString(", ")

                            binding.address.text = if (fullAddress.isNotEmpty()) {
                                fullAddress
                            } else {
                                "Maimi, Alexandria, Egypt"
                            }

                            productsAdapter.submitList(result.data[index].products)

                            // Update the currency in the adapter whenever new data is set
                            productsAdapter.updateCurrency()
                        }

                        is ApiState.Failure -> {

                        }

                        ApiState.Init -> TODO()
                    }
                }
            }
        }
    }

    private fun formatPrice(price: Double): String {
        val currencyData = SharedPrefsService.getCurrencyData()
        val code = currencyData.first ?: "EGP" // Default to EGP if no currency found
        val rate = currencyData.third?.toDouble() ?: 1.0 // Convert rate to Double, default to 1.0 if null

        // Convert the price based on the current rate
        val newPrice = price * rate
        return String.format("%.2f %s", newPrice, code) // Format price to two decimal places and append currency code
    }

}