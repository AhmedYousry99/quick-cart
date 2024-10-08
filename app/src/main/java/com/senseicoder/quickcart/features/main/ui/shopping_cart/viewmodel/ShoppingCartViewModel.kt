package com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.repos.cart.CartRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShoppingCartViewModel(val repo: CartRepo) : ViewModel() {
    private val _cartProducts: MutableStateFlow<ApiState<List<ProductOfCart>?>> =
        MutableStateFlow(ApiState.Loading)
    val cartProducts = _cartProducts

    private val _removeProductFromCart: MutableStateFlow<ApiState<String?>> =
        MutableStateFlow(ApiState.Loading)
    val removeProductFromCart = _removeProductFromCart

    private val _updating : MutableSharedFlow<ApiState<List<ProductOfCart>?>> = MutableSharedFlow()
    val updating = _updating

    fun fetchCartProducts(cartId: String) {
        viewModelScope.launch {
            repo.getCartProducts(cartId).catch {
                _cartProducts.value = ApiState.Failure(it.message.toString())
            }.collect {
                _cartProducts.value = ApiState.Success(it)
            }
        }
    }

    fun deleteFromCart(cartId: String, lineItemId: String) {
        viewModelScope.launch {
            repo.removeProductFromCart(cartId, lineItemId).catch {
                _removeProductFromCart.value = ApiState.Failure(it.message.toString())
            }.collect {
                _removeProductFromCart.value = ApiState.Success(it)
            }
        }
    }

    fun updateQuantityOfProduct(cartId: String, lineId: String, quantity: Int){
        viewModelScope.launch {
            repo.updateQuantityOfProduct(cartId, lineId, quantity).catch {
                _updating.emit(ApiState.Failure(it.message.toString()))
            }.collect{
                _updating.emit(ApiState.Success(it))
            }
        }
    }
    fun refresh(cardId: String){
        _cartProducts.value = ApiState.Loading
        fetchCartProducts(cardId)
    }
}