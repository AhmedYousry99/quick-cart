package com.senseicoder.quickcart.core.models.repositories

import com.senseicoder.quickcart.core.models.CustomerDTO
import kotlinx.coroutines.flow.Flow

interface CustomerRepo {
    /*
    * $email: String!, $firstName: String!, $lastName:String!*/
    suspend fun signupUsingEmailAndPassword(firstName: String, lastName: String, email: String, password: String): Flow<CustomerDTO>
    suspend fun loginUsingNormalEmail(email: String, password: String): Flow<CustomerDTO>
    suspend fun loginUsingGuest(): Flow<CustomerDTO>
    fun setUserId(value: String)
    fun getUserId(): String
    fun setUserToken(token: String)
    fun getUserToken(): String
}