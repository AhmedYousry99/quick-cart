package com.senseicoder.quickcart.core.network.interfaces

import com.senseicoder.quickcart.core.model.ProductOfCart
import com.storefront.AddProductsToCartMutation
import com.storefront.CartLinesUpdateMutation
import com.storefront.CreateCartMutation
import com.storefront.CreateCustomerAccessTokenMutation
import com.storefront.CreateCustomerMutation
import com.storefront.RemoveProductFromCartMutation
import kotlinx.coroutines.flow.Flow

interface StorefrontHandler {
    suspend fun loginUser(
        email: String,
        password: String
    ): Flow<CreateCustomerAccessTokenMutation.CustomerAccessToken>

    fun createCustomer(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Flow<CreateCustomerMutation.Customer>

    suspend fun removeProductFromCart(cartId: String, lineId: String): Flow<RemoveProductFromCartMutation.CartLinesRemove?>

    suspend fun getProductsCart(cartId: String): Flow<List<ProductOfCart>?>

    suspend fun createCart(email: String, token: String): Flow<CreateCartMutation.Cart>

    suspend fun addToCartById(cartId: String, productsOfCart: List<ProductOfCart>): Flow<AddProductsToCartMutation.CartLinesAdd>

    suspend fun updateQuantityOfProduct(cartId: String, lineId: String,quantity:Int): Flow<CartLinesUpdateMutation.Lines?>
}