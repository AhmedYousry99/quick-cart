package com.senseicoder.quickcart.features.main.ui.product_details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// TODO ADD CART REPO AND REPO FOR SHARED PREF TO GET TOKEN IN CONSTRUCTOR
class DetailsViewModel : ViewModel() {

    private val _cardId: MutableStateFlow<ApiState<String?>> = MutableStateFlow(ApiState.Loading)
    val cardId: MutableStateFlow<ApiState<String?>> = _cardId

    private val _addingToCart: MutableStateFlow<ApiState<String?>> = MutableStateFlow(ApiState.Loading)
    val addingToCart: MutableStateFlow<ApiState<String?>> = _addingToCart

    private val _cartItemRemove: MutableStateFlow<ApiState<String?>> = MutableStateFlow(ApiState.Loading)
    val cartItemRemove: MutableStateFlow<ApiState<String?>> = _cartItemRemove

    fun createCart(email: String) {
        viewModelScope.launch {
//            repository.createCart(email, repository.readUserToken()).collect { response ->
//                _cartId.value = response
//            }
        }
    }

    fun addProductToCart(productId: String, quantity: Int, variantId: String) {

        viewModelScope.launch {
//            repository.addToCartById(productId, quantity, variantId).collect {
//                _addingToCart.value = it
//            }
        }
    }

    fun removeProductFromCart(cartId: String, lineId: String) {
        viewModelScope.launch {
//            repository.removeProductFromCart(cartId, lineId)
//                .collect {
//                    _cartItemRemove.value = it
//                }
        }
    }


}