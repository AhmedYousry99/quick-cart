package com.senseicoder.quickcart.features.main.ui.order.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.core.repos.order.OrderRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel (private val repository : OrderRepo) :ViewModel(){
    private val _apiState = MutableStateFlow<ApiState<List<Order>>>(ApiState.Loading)
    val apiState: StateFlow<ApiState<List<Order>>> = _apiState

    //private var _requiredCurrency = MutableStateFlow<ApiState<CurrencyResponse>>(ApiState.Loading)
    //val requiredCurrency = _requiredCurrency.asStateFlow()

    private val _currencyUnit = MutableStateFlow<String>("EGP")
    val currencyUnit = _currencyUnit.asStateFlow()

//    fun getCustomerOrders(token:String){
//        viewModelScope.launch {
//            repository.getCustomerOrders(token).collect{
//                _apiState.value = it
//            }
//        }
//    }
fun getCustomerOrders(token: String) {
    viewModelScope.launch {
        _apiState.value = ApiState.Loading // Emit loading state
        try {
            repository.getCustomerOrders(token).collect { response ->
                _apiState.value = response // Emit the response
            }
        } catch (e: Exception) {
            // Emit a failure state with the error message
            _apiState.value = ApiState.Failure("Failed to fetch customer orders: ${e.message ?: "Unknown error"}")
            Log.e("OrderViewModel", "Error fetching customer orders", e)
        }
    }
}

//    fun getCurrencyUnit() {
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _currencyUnit.value = repository.readCurrencyUnit(Constants.CURRENCY_UNIT)
//        }
//    }

//    fun getRequiredCurrency() {
//        Log.i(TAG, "getRequiredCurrency: ")
//        viewModelScope.launch(Dispatchers.IO) {
//
//            repository.getCurrencyRate(repository.readCurrencyUnit(Constants.CURRENCY_UNIT))
//                .catch { error ->
//                    _requiredCurrency.value = ApiState.Failure(error)
//                }
//                .collect { response ->
//                    _requiredCurrency.value =
//                        response ?: ApiState.Failure(Throwable("Something went wrong"))
//                }
//        }
//    }

    class OrdersViewModelFactory(
        private val repository: OrderRepo,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
                OrderViewModel(repository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
