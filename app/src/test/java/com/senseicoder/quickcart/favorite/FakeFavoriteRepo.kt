import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFavoriteRepo : FavoriteRepo {

    private val favorites = mutableListOf<FavoriteDTO>()
    var shouldReturnError = false

    var isUserIdInvalid  = false // New flag to simulate invalid Firebase ID


    override suspend fun addFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO> {
        return flow {
            if (shouldReturnError) {
                throw Exception("Failed to add favorite") // Simulate error
            }
            delay(100) // Simulating network delay
            val favorite = FavoriteDTO(
                id = product.id,
                title = product.title,
                image = product.images.map { it.url },
                priceMinimum = product.priceRange.minVariantPrice.amount.toString(),
                priceMaximum = product.priceRange.maxVariantPrice.amount.toString(),
                description = product.productType
            )
            favorites.add(favorite)
            emit(favorite) // Emit the favorite after adding
        }
    }
    override suspend fun removeFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO> {
        return flow {
            delay(100)
            val favoriteToRemove = favorites.find { it.id == product.id }
            if (favoriteToRemove != null) {
                favorites.remove(favoriteToRemove)
                emit(favoriteToRemove)
            } else {
                throw Exception("Favorite not found")
            }
        }
    }

    override suspend fun removeFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        return flow {
            delay(100)
            val favoriteToRemove = favorites.find { it.id == favorite.id }
            if (favoriteToRemove != null) {
                favorites.remove(favoriteToRemove)
                emit(favoriteToRemove)
            } else {
                throw Exception("Favorite not found")
            }
        }
    }

    override fun getUserFirebaseID(): String {
        return if (isUserIdInvalid) {
            "" // Simulate an invalid Firebase ID
        } else {
            "firebaseId" // Simulate a valid Firebase ID
        }
    }

    override fun getFavorites(firebaseId: String): Flow<List<FavoriteDTO>> {
        return flow {
            if (firebaseId.isEmpty()) {
                throw Exception("failed to get favorites") // Simulate failure if userId is invalid
            }

            // Create a list of favorites with empty fields for the test
            val favoritesToEmit = favorites.map {
                FavoriteDTO(
                    id = it.id,
                    title = it.title,
                    image = it.image,
                    description = "", // Set this to empty to match the test expectation
                    priceMinimum = "", // Set this to empty to match the test expectation
                    priceMaximum = "" // Set this to empty to match the test expectation
                )
            }
            emit(favoritesToEmit) // Emit the list of favorites
        }
    }

    override suspend fun isFavorite(firebaseId: String, productId: String): Flow<Boolean> {
        return flow {
            delay(100)
            emit(favorites.any { it.id == productId })
        }
    }

    // Helper method to simulate returning an invalid Firebase ID
    fun setUserFirebaseIDToReturnNull() {
        isUserIdInvalid = true
    }
}
