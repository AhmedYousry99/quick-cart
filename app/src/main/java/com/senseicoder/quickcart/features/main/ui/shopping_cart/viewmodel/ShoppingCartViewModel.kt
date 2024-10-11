package com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.model.fromEdges
import com.senseicoder.quickcart.core.model.toAddressOfCustomer
import com.senseicoder.quickcart.core.repos.cart.CartRepo
import com.senseicoder.quickcart.core.repos.coupons.CouponsRepo
import com.senseicoder.quickcart.core.repos.order.draft_order.DraftOrderRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShoppingCartViewModel(val repo: CartRepo, val draftOrderRepo: DraftOrderRepo,val couponsRepo : CouponsRepo) : ViewModel() {
    private val _cartProducts: MutableStateFlow<ApiState<List<ProductOfCart>?>> =
        MutableStateFlow(ApiState.Loading)
    val cartProducts = _cartProducts

    private val _defaultAddress: MutableStateFlow<ApiState<AddressOfCustomer>> =
        MutableStateFlow(ApiState.Loading)
    val defaultAddress = _defaultAddress


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
        viewModelScope.launch {
            repo.removeProductFromCart(cartId, lineItemId).catch {
                _removeProductFromCart.value = ApiState.Failure(it.message.toString())
            }.collect {
                _removeProductFromCart.value = ApiState.Success(it)
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


    private val _draftOrderCompletion: MutableStateFlow<ApiState<DraftOrderReqRes>> =
        MutableStateFlow(ApiState.Loading)
    val draftOrderCompletion = _draftOrderCompletion
    private val _draftOrderCreation: MutableStateFlow<ApiState<DraftOrderReqRes>> =
        MutableStateFlow(ApiState.Loading)
    val draftOrderCreation = _draftOrderCreation
    private val _sendInvoice: MutableStateFlow<ApiState<DraftOrderReqRes>> =
        MutableStateFlow(ApiState.Loading)
    val sendInvoice = _sendInvoice


    fun createDraftOrder(draftOrderReqRes: DraftOrderReqRes) {
        viewModelScope.launch {
            draftOrderRepo.createDraftOrder(draftOrderReqRes).catch {
                _draftOrderCreation.value = ApiState.Failure(it.message.toString())
            }.collect {
                if (it.body() != null)
                    _draftOrderCreation.value = ApiState.Success(it.body()!!)
                else
                    _draftOrderCreation.value = ApiState.Failure(it.message())
            }
        }
    }


    fun completeDraftOrder(draftOrderId: Long) {
        viewModelScope.launch {
            draftOrderRepo.completeDraftOrder(draftOrderId).catch {
                _draftOrderCompletion.value = (ApiState.Failure(it.message.toString()))
            }.collect {
                if (it.body() != null)
                    _draftOrderCompletion.value = ApiState.Success(it.body()!!)
                else
                    _draftOrderCompletion.value = ApiState.Failure(it.message())
            }
        }
    }


    fun sendInvoice(orderId: Long) {

        viewModelScope.launch {
            draftOrderRepo.sendInvoice(orderId).catch {
                _sendInvoice.value = ApiState.Failure(it.message.toString())
            }.collect { res ->
                if (res.isSuccessful && res.body() != null)
                    _sendInvoice.value = ApiState.Success(res.body()!!)
                else
                    _sendInvoice.value = ApiState.Failure(res.message())

            }
        }
    }

     fun getAddress() {
        viewModelScope.launch {
            draftOrderRepo.getCustomerAddresses().first() {
                if (it != null) {
                    _defaultAddress.value =
                        (ApiState.Success(it.defaultAddress?.toAddressOfCustomer()!!))
                    true
                } else {
                    _defaultAddress.value = (ApiState.Failure("No default address"))
                    false
                }
            }
        }
    }

    private val _couponDetails: MutableStateFlow<ApiState<DiscountCodesResponse>> =
        MutableStateFlow(ApiState.Loading)
    val couponDetails: MutableStateFlow<ApiState<DiscountCodesResponse>> = _couponDetails

    fun checkCouponDetails(couponsId: String) {
        viewModelScope.launch {
            couponsRepo.checkCouponDetails(couponsId).catch {
                _couponDetails.value = ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN)
            }.collect {
                _couponDetails.value = ApiState.Success(it)
            }
        }

    }


    fun refresh(cardId: String) {
        _cartProducts.value = ApiState.Loading
        fetchCartProducts(cardId)
    }
}