package com.senseicoder.quickcart.core.repos.favorite

import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import kotlinx.coroutines.flow.Flow

interface FavoriteRepo {

    fun addFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO>
    fun removeFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO>
    fun removeFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO>
    fun getUserFirebaseID(): String
    fun getFavorites(firebaseId: String): Flow<List<FavoriteDTO>>
    fun isFavorite(firebaseId: String, productId :String): Flow<Boolean>
    suspend fun convertPricesAccordingToCurrency(favorite: FavoriteDTO): FavoriteDTO
    suspend fun revertPricesAccordingToCurrency(product: ProductDTO): ProductDTO
}