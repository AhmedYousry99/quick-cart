import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeFavoriteRepo : FavoriteRepo {

    private val favorites = mutableListOf<FavoriteDTO>()
    private var shouldReturnError = false // Make this private

    var isUserIdInvalid = false // New flag to simulate invalid Firebase ID

    // Public method to control the error simulation for testing
    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun addFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO> {
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

    override fun removeFavorite(firebaseId: String, product: ProductDTO): Flow<FavoriteDTO> {
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

    override fun removeFavorite(firebaseId: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
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
                throw Exception("Failed to get favorites") // Simulate failure if userId is invalid
            }
            delay(100) // Simulating network delay before emitting
            emit(favorites.toList()) // Emit the list of favorites
        }
    }

    override fun isFavorite(firebaseId: String, productId: String): Flow<Boolean> {
        return flow {
            delay(100)
            emit(favorites.any { it.id == productId })
        }
    }

    override suspend fun convertPricesAccordingToCurrency(favorite: FavoriteDTO): FavoriteDTO {
        // TODO: Implement this if needed for testing
        return favorite
    }

    override suspend fun revertPricesAccordingToCurrency(product: ProductDTO): ProductDTO {
        // For testing, return the product as is
        return product
    }
}
