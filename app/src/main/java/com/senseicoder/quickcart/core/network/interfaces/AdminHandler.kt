package com.senseicoder.quickcart.core.network.interfaces

import com.admin.CreateCustomerMutation
import com.admin.GetCustomerQuery
import com.admin.GetOrderDetailsQuery
import com.admin.ProductsQuery
import com.admin.UpdateCustomerMutation
import kotlinx.coroutines.flow.Flow

interface AdminHandler {

    fun getProducts(query: String): Flow<ProductsQuery.Data>
    fun createCustomer(email:String, firstName: String, lastName:String): Flow<CreateCustomerMutation.Data>
    fun getCustomer(id: String): Flow<GetCustomerQuery.Data>
    fun updateCustomer(
        email: String,
        firstName: String,
        lastName: String,
        id: String
    ): Flow<UpdateCustomerMutation.Data>

    fun getOrderDetails(id: String = "gid://shopify/Order/8163661185190"): Flow<GetOrderDetailsQuery.Data>
}