package com.senseicoder.quickcart.features.main.ui.product_details.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.repos.cart.CartRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val repository: CartRepo) : ViewModel() {

    private val _cartId: MutableStateFlow<ApiState<String?>> = MutableStateFlow(ApiState.Init)
    val cartId: MutableStateFlow<ApiState<String?>> = _cartId

    private val _addingToCart: MutableStateFlow<ApiState<List<ProductOfCart>>> =
        MutableStateFlow(ApiState.Init)
    val addingToCart: MutableStateFlow<ApiState<List<ProductOfCart>>> = _addingToCart

    private val _cartItemRemove: MutableStateFlow<ApiState<String?>> =
        MutableStateFlow(ApiState.Init)
    val cartItemRemove: MutableStateFlow<ApiState<String?>> = _cartItemRemove

    fun createCart(email: String) {
        viewModelScope.launch {
            repository.createCart(email, repository.getUserToken()).catch {
                _cartId.emit(ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN))
            }.collect { cartId ->
                repository.setCartId(cartId)
                _cartId.value = ApiState.Success(cartId)
            }
        }

        fun addProductToCart(productId: String, products: List<ProductOfCart>) {
            viewModelScope.launch {
                repository.addToCartByIds(productId, products).catch {
                    _cartId.emit(ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN))
                }.collect {
                    _addingToCart.value = ApiState.Success(it)
                }
            }
        }

        fun removeProductFromCart(cartId: String, lineId: String) {
            viewModelScope.launch {
                repository.removeProductFromCart(cartId, lineId).catch {
                    _cartId.emit(ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN))
                }
                    .collect {
                        _cartItemRemove.value = ApiState.Success(it)
                    }
            }
        }

    }
}