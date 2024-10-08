package com.senseicoder.quickcart.core.repos.cart

import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.storefront.CreateCartMutation
import kotlinx.coroutines.flow.Flow

interface CartRepo {
    suspend fun createCart(email: String, token: String): Flow<String>
    fun getUserToken(): String

    suspend fun getCartProducts(cartId: String): Flow<List<ProductOfCart>>
    suspend fun removeProductFromCart(
        cartId: String,
        lineId: String
    ): Flow<String>

    suspend fun addToCartByIds(
        cartId: String,
        productsOfCart: List<ProductOfCart>
    ): Flow<List<ProductOfCart>>

    fun setCartId(cartId: String)
    fun getCartId(): String
}