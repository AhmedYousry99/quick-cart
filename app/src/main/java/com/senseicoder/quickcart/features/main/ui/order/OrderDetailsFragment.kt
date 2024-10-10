package com.senseicoder.quickcart.features.main.ui.order

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

    }

    private fun collectOrderDetails() {
        val index = arguments?.getInt("index") ?: 0
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apiState.collect { result ->
                    when (result) {
                        is ApiState.Loading -> {

                        }

                        is ApiState.Success -> {

                            binding.totalPrice.text = result.data[index].totalPriceAmount+"EGP"
                            binding.subTotal.text = result.data[index].subTotalPriceAmount+"EGP"
                            binding.tax.text = result.data[index].totalTaxAmount+"EGP"
                            binding.orderId.text = result.data[index].name
                            binding.itemsCount.text =
                                "${result.data[index].products.count()} item"

//                            binding.address.text =
//                                result.data[index].address?: "Maimi,Alexandria,Egypt"

                            // Set address, concatenating address1, city, and country
                            val address = result.data[index].address
                            val fullAddress = listOfNotNull(address?.address1, address?.city, address?.country).joinToString(", ")

                            binding.address.text = if (fullAddress.isNotEmpty()) {
                                fullAddress
                            } else {
                                "Maimi, Alexandria, Egypt"
                            }
                            //getCurrentCurrency(
//                                result.data[index].totalTaxAmount.toDouble(),
//                                result.data[index].subTotalPriceAmount.toDouble(),
//                                result.data[index].totalPriceAmount.toDouble()
                           // )
                            productsAdapter.submitList(result.data[index].products)
                        }

                        is ApiState.Failure -> {

                        }

                        ApiState.Init -> TODO()
                    }
                }
            }
        }
    }

//    private fun getCurrentCurrency(tax: Double, subTotal: Double, totalPrice: Double) {
//        viewModel.getCurrencyUnit()
//        viewModel.getRequiredCurrency()
//
//        lifecycleScope.launch {
//            combine(
//                viewModel.currencyUnit,
//                viewModel.requiredCurrency
//            ) { currencyUnit, requiredCurrency ->
//                Pair(currencyUnit, requiredCurrency)
//            }.collect { (currencyUnit, requiredCurrency) ->
//                Log.i(TAG, "getCurrentCurrency 000: $currencyUnit")
//                when (requiredCurrency) {
//                    is ApiState.Failure -> Log.i(TAG, "getCurrentCurrency: ${requiredCurrency.msg}")
//                    ApiState.Loading -> Log.i(TAG, "getCurrentCurrency: Loading")
//                    is ApiState.Success -> {
//
//                        requiredCurrency.response.data[currencyUnit]?.let { currency ->
//                            binding.tax.text = String.format("%.2f %s", tax * currency.value, currency.code)
//                            binding.subTotal.text =
//                                String.format("%.2f %s", subTotal * currency.value, currency.code)
//                            binding.totalPrice.text =
//                                String.format("%.2f %s", totalPrice * currency.value, currency.code)
//
//                        }
//                    }
//                }
//            }
//        }
//    }


}