package com.senseicoder.quickcart.core.network.interfaces

import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.Flow

interface ICartRepo {
    suspend fun createCart(email: String, token: String): Flow<ApiState<String?>>
    suspend fun addToCartById(
        cartId: String,
        quantity: Int,
        variantID: String
    ): Flow<ApiState<String?>>

    suspend fun getCartProducts(cartId: String): Flow<ApiState<List<ProductOfCart>>>
    suspend fun removeProductFromCart(
        cartId: String,
        lineId: String
    ): Flow<ApiState<String?>>
}