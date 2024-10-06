package com.senseicoder.quickcart.core.network.interfaces

import com.senseicoder.quickcart.core.models.CustomerDTO
import kotlinx.coroutines.flow.Flow

interface FirebaseHandler {

    suspend fun signupUsingNormalEmail(email: String, password: String, firstName: String, lastName: String): Flow<CustomerDTO>

    suspend fun loginUsingNormalEmail(
        email: String,
        password: String,
    ): Flow<CustomerDTO>


//    fun loginUsingGoogleEmail(idToken: String?)
//
//    fun loginUsingFacebookEmail()

    suspend fun loginUsingGuest(): Flow<CustomerDTO>

    fun signOut()

//    fun isUserLoggedIn(): Boolean
//
//    fun isUserGuest(): Boolean
}