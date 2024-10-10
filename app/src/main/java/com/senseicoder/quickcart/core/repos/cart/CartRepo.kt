package com.senseicoder.quickcart.core.repos.cart

import com.senseicoder.quickcart.core.model.ProductOfCart
import kotlinx.coroutines.flow.Flow

interface CartRepo {
    suspend fun createCart(email: String): Flow<String>
    fun getUserToken(): String

    suspend fun getCartProducts(cartId: String): Flow<List<ProductOfCart>>
    suspend fun removeProductFromCart(
        cartId: String,
        lineId: String
    ): Flow<String?>
    fun getSharedPrefString(key: String, defaultValue: String): String

    suspend fun addToCartByIds(
        cartId: String,
        quantity: Int,
        variantId: String
    ): Flow<List<ProductOfCart>>

    fun setCartId(cartId: String)
    fun getCartId(): String
    fun updateQuantityOfProduct(cartId: String, lineId: String,quantity :Int): Flow<List<ProductOfCart>?>
}