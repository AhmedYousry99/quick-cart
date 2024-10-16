package com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.repos.address.AddressRepo
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepo
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.main.ui.profile.ProfileFragment
import com.senseicoder.quickcart.features.main.ui.profile.ProfileFragment.Companion
import com.storefront.CustomerAddressesQuery
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainActivityViewModel(private val currencyRepo: CurrencyRepo,private val addressRepo:AddressRepo) : ViewModel() {
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

    private val _currency : MutableSharedFlow<ApiState<CurrencyResponse>> = MutableStateFlow(ApiState.Loading)
    val currency = _currency.asSharedFlow()
    fun getCurrencyRate(newCurrency: String) {
        viewModelScope.launch{
            _currency.emit( ApiState.Loading)
            try {
                val res = currencyRepo.getCurrencyRate(newCurrency)
                Log.d(TAG, "prepareCurrencyDataAndSetListener: ")
                _currency.emit(ApiState.Success(res))
            }catch (e:Exception) {
                _currency.emit(ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN))
            }
        }

    }

    private val _allAddresses: MutableStateFlow<ApiState< CustomerAddressesQuery. Customer>> = MutableStateFlow(ApiState.Loading)
    val allAddresses = _allAddresses

    fun getCustomerAddresses() {
        _allAddresses.value = ApiState.Loading
        viewModelScope.launch {
            addressRepo.getCustomerAddresses().catch {
                _allAddresses.value = ApiState.Failure(it.message.toString())
            }.collect {
                if (it  != null)
                    _allAddresses.value = ApiState.Success(it)
                else
                    _allAddresses.value = ApiState.Failure("No data found")
            }
        }
    }

    fun updateAllAddress(new :ApiState< CustomerAddressesQuery. Customer>){
        _allAddresses.value = new
    }



    companion object{
        private const val TAG = "MainActivityViewModel"
        const val INIT: String = "init"
    }

}