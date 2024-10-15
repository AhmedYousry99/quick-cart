package com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.model.fromEdges
import com.senseicoder.quickcart.core.model.toAddressOfCustomer
import com.senseicoder.quickcart.core.repos.cart.CartRepo
import com.senseicoder.quickcart.core.repos.coupons.CouponsRepo
import com.senseicoder.quickcart.core.repos.order.draft_order.DraftOrderRepo
import com.senseicoder.quickcart.core.repos.payment.PaymentRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShoppingCartViewModel(val repo: CartRepo, val draftOrderRepo: DraftOrderRepo,val couponsRepo : CouponsRepo,
    val paymentRepo : PaymentRepo
) : ViewModel() {
    private val _cartProducts: MutableStateFlow<ApiState<List<ProductOfCart>?>> =
        MutableStateFlow(ApiState.Loading)
    val cartProducts = _cartProducts

    private val _defaultAddress: MutableStateFlow<ApiState<AddressOfCustomer>> =
        MutableStateFlow(ApiState.Loading)
    val defaultAddress = _defaultAddress.asStateFlow()


    fun fetchCartProducts(cartId: String) {
        viewModelScope.launch {
            repo.getCartProducts(cartId).catch {
                _cartProducts.value = ApiState.Failure(it.message.toString())
            }.collect {
                _cartProducts.value = ApiState.Success(it.data?.cart?.lines?.edges.fromEdges())
            }
        }
    }

    private val _removeProductFromCart: MutableStateFlow<ApiState<String?>> =
        MutableStateFlow(ApiState.Loading)
    val removeProductFromCart = _removeProductFromCart
    fun deleteFromCart(cartId: String, lineItemId: String) {
        _cartProducts.value = ApiState.Loading
        viewModelScope.launch {
            repo.removeProductFromCart(cartId, lineItemId).catch {
                _removeProductFromCart.value = ApiState.Failure(it.message.toString())
            }.collect {
                _removeProductFromCart.value = ApiState.Success(it)
                fetchCartProducts(cartId)
            }
        }
    }

    private val _updating: MutableSharedFlow<ApiState<List<ProductOfCart>?>> = MutableSharedFlow()
    val updating = _updating
    fun updateQuantityOfProduct(cartId: String, lineId: String, quantity: Int) {
        viewModelScope.launch {
            repo.updateQuantityOfProduct(cartId, lineId, quantity).catch {
                _updating.emit(ApiState.Failure(it.message.toString()))
            }.collect {
                _updating.emit(ApiState.Success(it))
            }
        }
    }


    private val _draftOrderCompletion: MutableSharedFlow<ApiState<DraftOrderReqRes>> = MutableSharedFlow()
    val draftOrderCompletion = _draftOrderCompletion.asSharedFlow()

    private val _draftOrderCreation: MutableSharedFlow<ApiState<DraftOrderReqRes>> =
        MutableSharedFlow()
    val draftOrderCreation = _draftOrderCreation .asSharedFlow()



    fun createDraftOrder(draftOrderReqRes: DraftOrderReqRes) {
        viewModelScope.launch {
            draftOrderRepo.createDraftOrder(draftOrderReqRes).catch {
                Log.d(TAG, "createDraftOrder: ${it.message}")
                _draftOrderCreation.emit(value = ApiState.Failure(it.message.toString()))
            }.collect() {
                Log.d(TAG, "createDraftOrder: ${it.errorBody()} \n ${it.body()}")
                if (it.body() != null) {
                    _draftOrderCreation.emit(value = ApiState.Success(it.body()!!))
                }
                else {
                    _draftOrderCreation.emit(value = ApiState.Failure(it.message()))
                }
            }
        }
    }


    fun completeDraftOrder(draftOrderId: Long) {
        viewModelScope.launch {
            draftOrderRepo.completeDraftOrder(draftOrderId).catch {
                Log.d(TAG, "completeDraftOrder: ${it.message}")
                _draftOrderCompletion.emit(value = (ApiState.Failure(it.message.toString())))
            }.collect() {
                Log.d(TAG, "completeDraftOrder: ${it.errorBody()} \n ${it.body()}")
                if (it.body() != null) {
                    _draftOrderCompletion.emit(value = ApiState.Success(it.body()!!))
                }
                else {
                    _draftOrderCompletion.emit(value = ApiState.Failure(it.errorBody().toString()))
                }
            }
        }
    }


     fun getAddress() {
        viewModelScope.launch {
            try{
                draftOrderRepo.getCustomerAddresses().catch {
                    _defaultAddress.value = ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN)
                }.first() {
                    if (it != null) {
                        _defaultAddress.value =
                            (ApiState.Success(it.defaultAddress?.toAddressOfCustomer()!!))
                        true
                    } else {
                        _defaultAddress.value = (ApiState.Failure("No default address"))
                        false
                    }
                }
            }catch (e : Exception) {
                _defaultAddress.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
            }
        }
    }

    private val _couponDetails: MutableSharedFlow<ApiState<PriceRulesResponse>> =
        MutableStateFlow(ApiState.Loading)
    val couponDetails = _couponDetails.asSharedFlow()

    fun fetchCoupons() {
        viewModelScope.launch {
            couponsRepo.fetchCoupons().catch {
                _couponDetails.emit(value = ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN))
            }.first {
                if (it.price_rules.isNotEmpty()) {
                    _couponDetails.emit(value = ApiState.Success(it))
                    true
                } else {
                    _couponDetails.emit(value = ApiState.Failure("No coupons found"))
                    false
                }
            }
        }

    }


companion object{
    private const val TAG = "ShoppingCartViewModel"
}
}