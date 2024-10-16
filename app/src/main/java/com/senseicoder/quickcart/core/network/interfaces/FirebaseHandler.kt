package com.senseicoder.quickcart.core.network.interfaces

import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import kotlinx.coroutines.flow.Flow

interface FirebaseHandler {

    fun signupUsingNormalEmail(email: String, password: String, firstName: String, lastName: String): Flow<CustomerDTO>

    fun loginUsingNormalEmail(
        email: String,
        password: String,
    ): Flow<CustomerDTO>
    fun updateDisplayName(
        customerDTO: CustomerDTO
    ): Flow<CustomerDTO>


    fun handleEmailVerification(customer: CustomerDTO): Flow<CustomerDTO>

    fun sendEmailVerification(customer: CustomerDTO): Flow<CustomerDTO>

//    fun loginUsingGoogleEmail(idToken: String?)

    fun signOut()

}