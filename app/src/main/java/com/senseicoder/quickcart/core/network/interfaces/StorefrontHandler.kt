package com.senseicoder.quickcart.core.network.interfaces

import com.senseicoder.quickcart.core.model.ProductOfCart
import com.storefront.AddProductsToCartMutation
import com.storefront.CreateCartMutation
import com.storefront.CreateCustomerAccessTokenMutation
import com.storefront.CreateCustomerMutation
import com.storefront.GetCartDetailsQuery
import kotlinx.coroutines.flow.Flow

interface StorefrontHandler {
    suspend fun loginUser(email: String, password: String): Flow<CreateCustomerAccessTokenMutation.CustomerAccessToken>
    fun createCustomer(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Flow<CreateCustomerMutation.Customer>
    suspend fun createCart(email: String, token: String): Flow<CreateCartMutation.Cart>
    suspend fun getCartProducts(cartId: String): Flow<GetCartDetailsQuery.Cart>
    suspend fun removeProductFromCart(cartId: String, lineId: String): Flow<String>
    suspend fun addToCartById(cartId: String, productsOfCart: List<ProductOfCart>): Flow<AddProductsToCartMutation.CartLinesAdd>
}