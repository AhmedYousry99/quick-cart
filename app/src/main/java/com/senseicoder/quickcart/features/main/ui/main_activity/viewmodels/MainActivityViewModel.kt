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
        _currentProductId.value = "${Constants.API.PRODUCT_ID_PREFIX}$id"
    }

    fun updateCurrentUser(customerDTO: CustomerDTO) {
        _currentUser.value = customerDTO
    }

    companion object{
        const val INIT: String = "init"
    }
}