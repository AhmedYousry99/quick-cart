package com.senseicoder.quickcart.core.repos.customer

import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import kotlinx.coroutines.flow.Flow

interface CustomerRepo {
    /*
    * $email: String!, $firstName: String!, $lastName:String!*/
    suspend fun signupUsingEmailAndPassword(firstName: String, lastName: String, email: String, password: String): Flow<CustomerDTO>
    suspend fun loginUsingNormalEmail(email: String, password: String): Flow<CustomerDTO>
    suspend fun loginUsingGuest(): Flow<CustomerDTO>
    fun signOut()
    fun setUserId(value: String)
    fun getUserId(): String
    fun setUserToken(token: String)
    fun getUserToken(): String
    fun setEmail(email: String)
    fun setDisplayName(displayName: String)
    fun setCartId(cartId: String)
    fun getCartId(): String
    fun setFirebaseId(firebaseId: String)
    suspend fun addFavorite(email:String, favorite: FavoriteDTO): Flow<FavoriteDTO>
    suspend fun removeFavorite(email:String, favorite: FavoriteDTO): Flow<FavoriteDTO>
}