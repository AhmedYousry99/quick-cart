package com.senseicoder.quickcart.core.repos.favorite

import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import kotlinx.coroutines.flow.Flow

interface FavoriteRepo {

    suspend fun addFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO>
    suspend fun removeFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO>
    suspend fun removeFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO>
    fun getUserFirebaseID(): String
    fun getFavorites(firebaseId: String): Flow<List<FavoriteDTO>>
    suspend fun isFavorite(firebaseId: String, productId :String): Flow<Boolean>
}