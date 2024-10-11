package com.senseicoder.quickcart.features.main.ui.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.repos.order.OrderRepo
import com.senseicoder.quickcart.core.repos.order.OrderRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentOrderBinding
import com.senseicoder.quickcart.features.main.ui.order.OrderFragmentDirections
import com.senseicoder.quickcart.features.main.ui.order.viewmodel.OrderViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class OrderFragment : Fragment() {

    companion object {
        private const val TAG = "OrdersFragment"
    }

    private lateinit var binding: FragmentOrderBinding
    private lateinit var ordersManager: LinearLayoutManager
    private lateinit var ordersAdapter: OrderAdapter
    private lateinit var viewModel: OrderViewModel
    private lateinit var repo: OrderRepo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setUpOrdersAdapter()
        collectOrders()



    }

    private fun setupNoOrders(items:List<Order>) {
        if (items.isEmpty()){
            binding.noOrders.visibility = View.VISIBLE
        }
    }

    private fun setUpOrdersAdapter() {
        ordersAdapter = OrderAdapter(requireContext()) { index ->
            val action = OrderFragmentDirections.actionOrderFragmentToOrderDetailsFragment(index)
            findNavController().navigate(action)
        }
        binding.ordersRV.layoutManager = LinearLayoutManager(requireContext())
        binding.ordersRV.adapter = ordersAdapter
    }
//    private fun setUpOrdersAdapter() {
//        ordersAdapter = OrderAdapter(
//            requireContext()
//        ) { index ->
//            val action =
//                OrderFragmentDirections.actionOrderFragmentToOrderDetailsFragment(index)
//            findNavController().navigate(action)
//        }

//        ordersManager = LinearLayoutManager(requireContext())
//        ordersManager.orientation = LinearLayoutManager.VERTICAL
//        binding.ordersRV.layoutManager = ordersManager
//        binding.ordersRV.adapter = ordersAdapter
//        binding.ordersRV.layoutManager = LinearLayoutManager(requireContext())
//        binding.ordersRV.adapter = ordersAdapter
//    }


//    private fun collectOrders() {
//        lifecycleScope.launch {
//            viewModel
//                .getCustomerOrders(repo.readUserToken())
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.apiState.collect { result ->
//                    when (result) {
//                        is ApiState.Loading -> {
//                            binding.shimmerFrameLayoutOrders.startShimmer()
//                        }
//
//                        is ApiState.Success -> {
//                            binding.shimmerFrameLayoutOrders.stopShimmer()
//                            binding.shimmerFrameLayoutOrders.visibility = View.GONE
//                            setupNoOrders(result.data)
//                            ordersAdapter.submitList(result.data)
//                           // getCurrentCurrency()
//                        }
//
//                        is ApiState.Failure -> {
//
//                        }
//
//                        ApiState.Init -> TODO()
//                    }
//                }
//            }
//        }
//    }
private fun collectOrders() {
    lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getCustomerOrders("7beaf58f597b3ce62a552fc6b221ab4a") // Replace with token retrieval
            viewModel.apiState.collect { result ->
                when (result) {
                    is ApiState.Loading -> {
                        // Show loading state
                        binding.shimmerFrameLayoutOrders.startShimmer()
                        binding.shimmerFrameLayoutOrders.visibility = View.VISIBLE
                        binding.noOrders.visibility = View.GONE
                        binding.ordersRV.visibility = View.GONE
                        Log.d(TAG, "State: Loading")
                    }
                    is ApiState.Success -> {
                        // Show success state
                        binding.shimmerFrameLayoutOrders.stopShimmer()
                        binding.shimmerFrameLayoutOrders.visibility = View.GONE
                        binding.ordersRV.visibility = View.VISIBLE

                        Log.d(TAG, "State: Success with orders: ${result.data}")

                        if (result.data.isEmpty()) {
                            binding.noOrders.visibility = View.VISIBLE
                            binding.ordersRV.visibility = View.GONE
                            Log.d(TAG, "No orders found")
                        } else {
                            binding.noOrders.visibility = View.GONE
                            ordersAdapter.submitList(result.data) // Submit the list to the adapter
                            Log.d(TAG, "Orders submitted to adapter: ${result.data.size} orders")
                        }
                    }
                    is ApiState.Failure -> {
                        // Show failure state
                        binding.shimmerFrameLayoutOrders.stopShimmer()
                        binding.shimmerFrameLayoutOrders.visibility = View.GONE
                        binding.noOrders.visibility = View.GONE
                        binding.ordersRV.visibility = View.GONE

                        Toast.makeText(requireContext(), result.msg, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "State: Failure, Error: ${result.msg}")
                    }
                    ApiState.Init -> {
                        // Handle initial state if needed
                        binding.shimmerFrameLayoutOrders.visibility = View.GONE
                        binding.noOrders.visibility = View.GONE
                        binding.ordersRV.visibility = View.GONE
                    }

                    else -> {}
                }
            }
        }
    }
}

    private fun setUpViewModel() {
//        repo = ShopifyRepositoryImpl(
//            ShopifyRemoteDataSourceImpl.getInstance(requireContext()),
//            SharedPreferencesImpl.getInstance(requireContext()),
//            CurrencyRemoteDataSourceImpl.getInstance(), AdminRemoteDataSourceImpl.getInstance()
//        )


        // Get instances of the required dependencies
        val storefrontHandler = StorefrontHandlerImpl
        val sharedPreferences = SharedPrefsService
       // val sharedPreferences = SharedPreferencesImpl.getInstance(requireContext())

        repo = OrderRepoImpl(storefrontHandler , sharedPreferences)
        val factory = OrderViewModel.OrdersViewModelFactory(repo)

        viewModel = ViewModelProvider(requireActivity(), factory).get(OrderViewModel::class.java)
    }


//    private fun getCurrentCurrency() {
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
//                        Log.i(
//                            TAG,
//                            "getCurrentCurrency: ${requiredCurrency.response.data[currencyUnit]?.code}"
//                        )
//                        requiredCurrency.response.data[currencyUnit]?.let { currency ->
//                            Log.i(TAG, "getCurrentCurrency: ${currency.value}")
//                            ordersAdapter.updateCurrentCurrency(currency.value, currency.code)
//                        }
//                    }
//                }
//            }
//        }
//    }


}