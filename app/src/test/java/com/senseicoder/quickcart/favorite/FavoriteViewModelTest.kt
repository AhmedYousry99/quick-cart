import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.FeaturedImage
import com.senseicoder.quickcart.core.model.graph_product.Price
import com.senseicoder.quickcart.core.model.graph_product.PriceRange
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.main.ui.favorite.viewmodel.FavoriteViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoriteViewModelTest {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var fakeFavoriteRepo: FakeFavoriteRepo

    @Before
    fun setUp() {
        fakeFavoriteRepo = FakeFavoriteRepo()
        viewModel = FavoriteViewModel(fakeFavoriteRepo)
    }

    @Test
    // Test to ensure the initial state is set to Init
    fun `getFavorites should emit Init state initially`() = runTest {
        // Act
        viewModel.getFavorites()

        // Assert: Expecting the initial state to be Init
        val result = viewModel.favorites.first()
        assertEquals(ApiState.Init, result)
    }


    @Test
    fun `getFavorites should update favorites state to Success with correct data`() = runTest {
        // Prepare the product data
        val product = ProductDTO(
            id = "123",
            title = "Test Product",
            description = "This is a test product.",
            productType = "Type",
            vendor = "Vendor",
            handle = "test-product",
            totalInventory = 100,
            options = emptyList(),
            priceRange = PriceRange(
                maxVariantPrice = Price(amount = "100.0", currencyCode = "USD"),
                minVariantPrice = Price(amount = "50.0", currencyCode = "USD")
            ),
            variants = emptyList(),
            images = listOf(FeaturedImage(url = "http://example.com/image.png")),
            rating = 4.5,
            reviewCount = 10,
            currency = "USD",
            totalCount = 1
        )

        // Simulate adding a favorite and collect the result
        val addedFavoriteFlow = fakeFavoriteRepo.addFavorite("firebaseId", product)

        // Collect to trigger emission and ensure it completes
        addedFavoriteFlow.collect { favorite ->
            assertEquals("123", favorite.id)
            assertEquals("Test Product", favorite.title)
        }

        // Now fetch favorites
        val favoritesFlow = fakeFavoriteRepo.getFavorites("firebaseId")

        favoritesFlow.collect { favorites ->
            assertTrue(favorites.isNotEmpty())
            assertEquals(1, favorites.size)
            assertEquals("Test Product", favorites[0].title) // Assert the correct title
        }
    }

    @Test
    fun `addToFavorite should emit Failure when an error occurs`() = runTest {
        // Arrange
        fakeFavoriteRepo.setShouldReturnError(true) // Simulate an error
        val product = createSampleProductDTO("invalid", "Invalid Product", "http://example.com/image.png")

        // Act
        viewModel.addToFavorite(product)
        advanceUntilIdle() // Advance the coroutine until idle to process all emissions

        // Assert
        val state = viewModel.isFavorite.first() // Collect the first emission
        assertTrue(state is ApiState.Failure)
        assertEquals("failed to add favorite", (state as ApiState.Failure).msg)
    }



    private fun createSampleProductDTO(id: String, title: String, imageUrl: String): ProductDTO {
        return ProductDTO(
            id = id,
            title = title,
            description = "This is a sample product.",
            productType = "Shoes",
            vendor = "Test Vendor",
            handle = "sample-product-$id",
            totalInventory = 10,
            priceRange = PriceRange(
                minVariantPrice = Price(amount = "100.0", currencyCode = "USD"),
                maxVariantPrice = Price(amount = "150.0", currencyCode = "USD")
            ),
            images = listOf(FeaturedImage(url = imageUrl)),
            rating = 4.5,
            reviewCount = 10
        )
    }
}
