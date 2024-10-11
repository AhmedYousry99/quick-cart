package com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels

import androidx.lifecycle.ViewModel
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel : ViewModel() {
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

    companion object{
        private const val TAG = "MainActivityViewModel"
        const val INIT: String = "init"
    }

}