package com.senseicoder.quickcart.core.network.interfaces

import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import kotlinx.coroutines.flow.Flow

interface CustomerAdminDataSource {
    suspend fun createCustomer(firstName: String, lastName: String, email: String, password: String): Flow<CustomerDTO>
    fun getCustomerById(customer: CustomerDTO): Flow<CustomerDTO>
}