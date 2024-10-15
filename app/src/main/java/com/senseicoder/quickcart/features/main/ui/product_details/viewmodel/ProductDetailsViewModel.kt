package com.senseicoder.quickcart.features.main.ui.product_details.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.model.ReviewDTO
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.model.graph_product.Variant
import com.senseicoder.quickcart.core.repos.cart.CartRepo
import com.senseicoder.quickcart.core.repos.product.ProductsRepo
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val cartRepo: CartRepo,
    private val productsRepo: ProductsRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _cartId: MutableStateFlow<ApiState<String?>> = MutableStateFlow(ApiState.Init)
    val cartId = _cartId.asStateFlow()

    private val reviews: List<ReviewDTO> = listOf(
        ReviewDTO(name = "Kareem", description = "Quality is not as advertised.", rating = 2.5),
        ReviewDTO(name = "Dina", description = "Very happy with my purchase.", rating = 4.5),
        ReviewDTO(name = "Ali", description = "Not bad.", rating = 3.0),
        ReviewDTO(name = "Layla", description = "Absolutely love it! Great buy.", rating = 5.0),
        ReviewDTO(
            name = "Tamer",
            description = "Product arrived in terrible condition.",
            rating = 1.0
        ),
        ReviewDTO(name = "Adel", description = "Not worth the price.", rating = 2.0),
        ReviewDTO(name = "Salma", description = "Exceeded my expectations!", rating = 5.0),
        ReviewDTO(name = "Ibrahim", description = "Wouldn't recommend.", rating = 2.0),
        ReviewDTO(name = "Reem", description = "Good, but shipping took too long.", rating = 3.5),
    )

    private val _addingToCart: MutableStateFlow<ApiState<List<ProductOfCart>>> =
        MutableStateFlow(ApiState.Init)
    val addingToCart = _addingToCart.asStateFlow()

    private val _product: MutableStateFlow<ApiState<ProductDTO>> =
        MutableStateFlow(ApiState.Init)
    val product = _product.asStateFlow()

    private val _selectedProduct: MutableSharedFlow<ProductState<Pair<List<Variant>, SelectedBy>>> =
        MutableSharedFlow(1)
    val selectedProduct = _selectedProduct.asSharedFlow()




    fun createCart(email: String) {
        viewModelScope.launch {
            cartRepo.createCart(email).catch {
                _cartId.emit(ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN))
            }.collect { cartId ->
                cartRepo.setCartId(cartId)
                _cartId.value = ApiState.Success(cartId)
            }
        }
    }

    fun getProductDetails(productId: String = "gid://shopify/Product/8309909618854") {
        viewModelScope.launch(dispatcher) {
            productsRepo.getProductDetailsGraph(productId).catch {
                _product.emit(ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN))
            }.map { productDTO ->
                val tempRating: Double =
                    reviews.fold(0.0) { acc: Double, review: ReviewDTO -> acc + review.rating }
                val tempCurrency = productsRepo.getCurrency()
                val product = productsRepo.convertPricesAccordingToCurrency(productDTO)
                product.copy(
                    rating = tempRating / if (reviews.isEmpty()) 1 else reviews.size,
                    currency = tempCurrency,
                    reviewCount = reviews.size
                )
            }.collect { product ->
                Log.d(
                    TAG,
                    "getProductDetails: ${
                        SharedPrefsService.logAllSharedPref(
                            TAG,
                            "getProductDetails"
                        )
                    }"
                )
                _product.value = ApiState.Success(product)
            }
        }
    }




    @OptIn(ExperimentalCoroutinesApi::class)
    fun addProductToCart(selectedAmount: Int, variants: List<Variant>, currentCustomer: CustomerDTO) {
        viewModelScope.launch(Dispatchers.Default) {
            _addingToCart.value = ApiState.Loading
            val id = cartRepo.getCartId()
            if(id != Constants.CART_ID_DEFAULT){
                Log.d(TAG, "addProductToCart: cartId exists: $id")
                cartRepo.addToCartByIds(id, selectedAmount, variants.first().id).catch {
                    _cartId.emit(ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN))
                }.collect {
                    _addingToCart.value = ApiState.Success(it)
                }
            }else{
                Log.d(TAG, "addProductToCart: creating ID")
                cartRepo.createCart(currentCustomer.email)
                    .flatMapConcat { cartId ->
                        cartRepo.addToCartByIds(cartId,selectedAmount, variants.first().id)
                    }.catch {
                        _cartId.emit(ApiState.Failure(it.message ?: Constants.Errors.UNKNOWN))
                    }.collect {
                            _addingToCart.value = ApiState.Success(it)
                    }
            }
        }
    }

    fun setCurrentSelectedProduct(
        variants: List<Variant>,
        isChecked: Boolean,
        selectedBy: SelectedBy,
        //value taken from the other group
        otherValue: CharSequence?,
        //value taken from this group
        currentValue: String
    ) {
        Log.d(TAG, "setCurrentSelectedProduct: ${selectedBy}, ${isChecked}, ${otherValue.isNullOrBlank()}\n${variants}")
        if (isChecked) {
            if (!otherValue.isNullOrBlank()) {
                Log.d(TAG, "setCurrentSelectedProduct: inside")
                filterAndSendSelectedProduct(variants, otherValue, currentValue)
            } else {
                sendVariants(variants, selectedBy, true, null)
            }
        } else {
            if (otherValue != null) {
                sendVariants(variants, selectedBy, false, otherValue)
            } else {
                resetToWholeProduct()
            }
        }
    }

    // case 2,3, 6
    private fun sendVariants(
        variants: List<Variant>,
        selectedBy: SelectedBy,
        isChecked: Boolean,
        value: CharSequence?
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            if (isChecked) {
                _selectedProduct.emit(ProductState.SingleSelected(Pair(variants, selectedBy)))
            } else {
                val matchingVariants =
                    (_product.value as ApiState.Success).data.variants.filter { it.selectedOptions.any { selectedOption -> selectedOption.value == value } }
                _selectedProduct.emit(ProductState.Unselected(Pair(matchingVariants, selectedBy)))
            }
        }
    }


    // case 1
    private fun resetToWholeProduct() {
        viewModelScope.launch {
            _selectedProduct.emit(ProductState.Reset)
        }
    }

    // case 5, 4
    private fun filterAndSendSelectedProduct(
        variants: List<Variant>,
        otherValue: CharSequence,
        currentValue: String
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val matchingVariants = variants.filter { variant ->
                variant.selectedOptions.any { selectedOption ->
                    selectedOption.value == otherValue
                } && variant.selectedOptions.any { selectedOption -> selectedOption.value == currentValue}
            }
            Log.d(TAG, "filterAndSendSelectedProduct: ->${variants}")

            Log.d(TAG, "filterAndSendSelectedProduct: ${currentValue}, ${otherValue}")
            _selectedProduct.emit(ProductState.MultiSelected(
                Pair(
                    matchingVariants,
                    if(_selectedProduct.replayCache.first() is ProductState.SingleSelected<Pair<List<Variant>, SelectedBy>>){
                        (_selectedProduct.replayCache.first()  as ProductState.SingleSelected<Pair<List<Variant>, SelectedBy>>).data.second
                    }else if(_selectedProduct.replayCache.first() is ProductState.MultiSelected<Pair<List<Variant>, SelectedBy>>){
                        (_selectedProduct.replayCache.first()  as ProductState.MultiSelected<Pair<List<Variant>, SelectedBy>>).data.second
                    }else{
                        (_selectedProduct.replayCache.first()  as ProductState.Unselected<Pair<List<Variant>, SelectedBy>>).data.second
                    }
                )
            ).also {
                Log.d(TAG, "filterAndSendSelectedProduct: ${it.data.first}")
            })
        }
    }

    companion object {
        private const val TAG = "ProductDetailsViewModel"
    }

    sealed class ProductState<out T> {
        data object Init: ProductState<Nothing>()
        data class MultiSelected<T>(val data: T): ProductState<T>()
        data class SingleSelected<T>(val data: T): ProductState<T>()
        data class Unselected<T>(val data: T): ProductState<T>()
        data object Reset: ProductState<Nothing>()
    }

    enum class SelectedBy {
        Color,
        Size
    }
}

private fun ProductOfCart.Companion.fromVariant(cartId: String, selectedAmount: Int, it: Variant): ProductOfCart {
    return ProductOfCart(
        id = cartId,
        quantity = selectedAmount,
        variantId = it.id
    )
}
