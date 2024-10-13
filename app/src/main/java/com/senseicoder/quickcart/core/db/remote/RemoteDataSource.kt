package com.senseicoder.quickcart.core.db.remote

import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {

    fun getUserByIdOrAddUser(customer: CustomerDTO): Flow<CustomerDTO>
    fun addUser(customer: CustomerDTO): Flow<CustomerDTO>
    fun getUserByEmail(customer: CustomerDTO): Flow<CustomerDTO>
    fun addFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO>
    fun isFavorite(firebaseId: String, productId :String): Flow<Boolean>
    fun removeFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO>
    fun getFavorites(firebaseId: String): Flow<List<FavoriteDTO>>
}