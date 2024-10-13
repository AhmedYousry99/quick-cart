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
) : FavoriteRepo {

    override  fun addFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO> {
        return dbRemoteDataSource.addFavorite(firebaseId, product.mapToFavoriteDTO())
            .timeout(15.seconds)
    }

    override fun removeFavorite(
        firebaseId: String,
        product: ProductDTO
    ): Flow<FavoriteDTO> {
        return dbRemoteDataSource.removeFavorite(firebaseId, product.mapToFavoriteDTO())
            .timeout(15.seconds)
    }

    override fun removeFavorite(
        firebaseId: String,
        favorite: FavoriteDTO
    ): Flow<FavoriteDTO> {
        return dbRemoteDataSource.removeFavorite(firebaseId, favorite).timeout(15.seconds)
    }

    override fun getUserFirebaseID(): String {
        return sharedPrefs.getSharedPrefString(
            Constants.FIREBASE_USER_ID,
            Constants.FIREBASE_USER_ID_DEFAULT
        )
    }

    override fun getFavorites(firebaseId: String): Flow<List<FavoriteDTO>> {
        return dbRemoteDataSource.getFavorites(firebaseId).timeout(15.seconds)
    }

    override fun isFavorite(firebaseId: String, productId: String): Flow<Boolean> {
        return dbRemoteDataSource.isFavorite(firebaseId, productId).timeout(15.seconds)
    }

    override suspend fun convertPricesAccordingToCurrency(favorite: FavoriteDTO): FavoriteDTO {
        val conversionRate = sharedPrefs.getSharedPrefFloat(
            Constants.PERCENTAGE_OF_CURRENCY_CHANGE,
            Constants.PERCENTAGE_OF_CURRENCY_CHANGE_DEFAULT
        )
        return favorite.copy(
            priceMaximum = (favorite.priceMaximum.toDouble() * conversionRate).toString(),
            priceMinimum = (favorite.priceMinimum.toDouble() * conversionRate).toString(),
        )

    }

    override suspend fun revertPricesAccordingToCurrency(product: ProductDTO): ProductDTO {
        val conversionRate = sharedPrefs.getSharedPrefFloat(
            Constants.PERCENTAGE_OF_CURRENCY_CHANGE,
            Constants.PERCENTAGE_OF_CURRENCY_CHANGE_DEFAULT
        )
        return product.copy(
            priceRange = product.priceRange.copy(
                maxVariantPrice = product.priceRange.maxVariantPrice.copy(
                    amount = (product.priceRange.maxVariantPrice.amount.toDouble() / conversionRate).toString()
                ),
                minVariantPrice = product.priceRange.minVariantPrice.copy(
                    amount = (product.priceRange.minVariantPrice.amount.toDouble() / conversionRate).toString()
                )
            ),
            variants = product.variants.map { variant ->
                variant.copy(
                    price = (variant.price.copy(
                        amount = (variant.price.amount.toDouble() / conversionRate).toString()
                    )
                            )
                )
            },
        )
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
