package com.senseicoder.quickcart.core.repos.cart

import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.senseicoder.quickcart.core.services.SharedPrefs
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.Flow

class CartRepoImpl(val remoteDataSource: StorefrontHandler,val sharedPref : SharedPrefs):
    CartRepo {
    override suspend fun createCart(email: String, token: String): Flow<ApiState<String?>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToCartById(
        cartId: String,
        quantity: Int,
        variantID: String
    ): Flow<ApiState<String?>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCartProducts(cartId: String): Flow<ApiState<List<ProductOfCart>>> {
        TODO("Not yet implemented")
    }

    override suspend fun removeProductFromCart(
        cartId: String,
        lineId: String
    ): Flow<ApiState<String?>> {
        TODO("Not yet implemented")
    }

}