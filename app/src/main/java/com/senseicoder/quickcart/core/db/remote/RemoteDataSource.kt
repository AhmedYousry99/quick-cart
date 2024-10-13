package com.senseicoder.quickcart.core.db.remote

import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {

    suspend fun getUserByIdOrAddUser(customer: CustomerDTO): Flow<CustomerDTO>
    suspend fun addUser(customer: CustomerDTO): Flow<CustomerDTO>
    suspend fun getUserByEmail(customer: CustomerDTO): Flow<CustomerDTO>
    suspend fun addFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO>
    suspend fun isFavorite(firebaseId: String, productId :String): Flow<Boolean>
    suspend fun removeFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO>
    fun getFavorites(firebaseId: String): Flow<List<FavoriteDTO>>
}