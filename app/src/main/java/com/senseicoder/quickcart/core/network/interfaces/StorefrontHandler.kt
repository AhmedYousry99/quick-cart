package com.senseicoder.quickcart.core.network.interfaces

import com.storefront.CreateCustomerAccessTokenMutation
import com.storefront.CreateCustomerMutation
import kotlinx.coroutines.flow.Flow

interface StorefrontHandler {
    suspend fun loginUser(email: String, password: String): Flow<CreateCustomerAccessTokenMutation.CustomerAccessToken>
    fun createCustomer(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Flow<CreateCustomerMutation.Customer>
}