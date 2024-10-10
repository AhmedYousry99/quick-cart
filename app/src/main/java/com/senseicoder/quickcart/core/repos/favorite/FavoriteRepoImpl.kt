package com.senseicoder.quickcart.core.repos.favorite

import com.senseicoder.quickcart.core.db.remote.FirebaseFirestoreDataSource
import com.senseicoder.quickcart.core.db.remote.RemoteDataSource
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.withoutGIDPrefix
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.services.SharedPrefs
import com.senseicoder.quickcart.core.services.SharedPrefsService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.timeout
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class FavoriteRepoImpl private constructor(
    private val sharedPrefs: SharedPrefs = SharedPrefsService,
    private val dbRemoteDataSource: RemoteDataSource = FirebaseFirestoreDataSource,
): FavoriteRepo {

    override suspend fun addFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO> {
        return dbRemoteDataSource.addFavorite(firebaseId, product.mapToFavoriteDTO()).timeout(15.seconds)
    }

    override suspend fun removeFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO> {
        return dbRemoteDataSource.removeFavorite(firebaseId, product.mapToFavoriteDTO()).timeout(15.seconds)
    }

    override suspend fun removeFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        return dbRemoteDataSource.removeFavorite(firebaseId, favorite).timeout(15.seconds)
    }

    override fun getUserFirebaseID(): String {
        return sharedPrefs.getSharedPrefString(Constants.FIREBASE_USER_ID, Constants.FIREBASE_USER_ID_DEFAULT)
    }

    override fun getFavorites(firebaseId: String): Flow<List<FavoriteDTO>> {
        return dbRemoteDataSource.getFavorites(firebaseId).timeout(15.seconds)
    }

    override suspend fun isFavorite(firebaseId: String, productId: String): Flow<Boolean> {
        return dbRemoteDataSource.isFavorite(firebaseId, productId).timeout(15.seconds)
    }


    companion object {
        private const val TAG = "CustomerRepoImpl"

        @Volatile
        private var instance: FavoriteRepoImpl? = null
        fun getInstance(
            sharedPrefs: SharedPrefs = SharedPrefsService,
            dbRemoteDataSource: RemoteDataSource = FirebaseFirestoreDataSource
        ): FavoriteRepoImpl {
            return instance ?: synchronized(this) {
                val instance =
                    FavoriteRepoImpl(
                        sharedPrefs,
                        dbRemoteDataSource
                    )
                Companion.instance = instance
                instance
            }
        }
    }
}

private fun ProductDTO.mapToFavoriteDTO(): FavoriteDTO {
    return FavoriteDTO(
        id.withoutGIDPrefix(),
        this.totalInventory.toString(),
        this.images.map { it.url },
        this.title,
        this.description,
        this.priceRange.minVariantPrice.amount,
        this.priceRange.maxVariantPrice.amount
    )
}
