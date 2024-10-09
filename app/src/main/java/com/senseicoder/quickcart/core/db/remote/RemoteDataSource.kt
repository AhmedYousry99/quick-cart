package com.senseicoder.quickcart.core.db.remote

import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {

    suspend fun getUserByIdOrAddUser(customer: CustomerDTO): Flow<CustomerDTO>
    suspend fun getUserByEmail(email: String): Flow<CustomerDTO>

}