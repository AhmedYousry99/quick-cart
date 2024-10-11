package com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepo
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainActivityViewModel(private val currencyRepo: CurrencyRepo) : ViewModel() {
    private val _currentProductId: MutableStateFlow<String> = MutableStateFlow(INIT)
    val currentProductId = _currentProductId

    private val _currentUser: MutableStateFlow<CustomerDTO> = MutableStateFlow(CustomerDTO(displayName = "", email = ""))
    val currentUser = _currentUser.asStateFlow()

    fun setCurrentProductId(id: String) {
        if(id.startsWith(Constants.API.PRODUCT_ID_PREFIX)){
            _currentProductId.value = id
        } else
            _currentProductId.value = "${Constants.API.PRODUCT_ID_PREFIX}$id"
    }

    fun updateCurrentUser(customerDTO: CustomerDTO) {
        _currentUser.value = customerDTO
    }

    private  val _location : MutableStateFlow<Pair<Double,Double>> = MutableStateFlow(Pair(0.0,0.0))
    val location = _location

    fun setLocation(lat:Double,long:Double){
        _location.value = Pair(lat,long)
    }

    private val _currency : MutableSharedFlow<ApiState<CurrencyResponse>> = MutableSharedFlow()
    val currency = _currency
    fun getCurrencyRate(newCurrency: String) {
        viewModelScope.launch{
            _currency.emit( ApiState.Loading)
            currencyRepo.getCurrencyRate(newCurrency).catch {
                _currency.emit(value = ApiState.Failure(it.message.toString()))
            }.collect {
                if (it.data.isEmpty())
                    _currency.emit(value = ApiState.Failure("IS EMPTY"))
                else {
                    _currency.emit(value = ApiState.Success(it))
                }
            }
        }

    }

    companion object{
        private const val TAG = "MainActivityViewModel"
        const val INIT: String = "init"
    }

}