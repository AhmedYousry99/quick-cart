import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.model.graph_product.FeaturedImage
import com.senseicoder.quickcart.core.model.graph_product.Price
import com.senseicoder.quickcart.core.model.graph_product.PriceRange
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.repos.favorite.FavoriteRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.main.ui.favorite.viewmodel.FavoriteViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
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
    //should test this first to initilaze before running the second test!
    fun `getFavorites should emit Init state initially`() = runTest {
        // Act
        viewModel.getFavorites()

        // Assert: Expecting the initial state to be Init
        val result = viewModel.favorites.first()
        assertEquals(ApiState.Init, result)
    }

    @Test
    fun `getFavorites should update favorites state to Success with correct data`() = runTest {
        // Arrange: Create sample ProductDTO objects
        val product1 = createSampleProductDTO("1", "Product 1", "url1")
        val product2 = createSampleProductDTO("2", "Product 2", "url2")

        // Add favorites to the fake repo
        fakeFavoriteRepo.addFavorite(fakeFavoriteRepo.getUserFirebaseID(), product1).first()
        fakeFavoriteRepo.addFavorite(fakeFavoriteRepo.getUserFirebaseID(), product2).first()

        // Act: Call getFavorites on the ViewModel
        viewModel.getFavorites()

        // Advance the coroutine until idle to ensure all emissions are processed
        advanceUntilIdle()

        // Assert: Expecting the favorites state to be Success with the correct data
        val result = viewModel.favorites.first()
        assertEquals(
            ApiState.Success(
                listOf(
                    FavoriteDTO(id = "1", title = "Product 1", image = listOf("url1"), description = "", priceMinimum = "", priceMaximum = ""),
                    FavoriteDTO(id = "2", title = "Product 2", image = listOf("url2"), description = "", priceMinimum = "", priceMaximum = "")
                )
            ),
            result
        )
    }

    @Test
    fun `addToFavorite should update isFavorite state to Success`() = runTest {
        // Arrange: Create a sample ProductDTO object to be added as favorite
        val product = createSampleProductDTO("3", "Product 3", "url3")

        // Assert: Expecting initial state to be Init
        val initialResult = viewModel.isFavorite.first()
        assertEquals(ApiState.Init, initialResult)

        // Act: Launch a coroutine to collect the isFavorite state
        val job = launch {
            viewModel.isFavorite.collect { result ->
                // Capture the emitted values
                if (result is ApiState.Success) {
                    assertEquals(true, result.data)
                    // Once we capture a success, we can cancel the collection to prevent further emissions
                    cancel()
                } else if (result is ApiState.Failure) {
                    fail("Expected Success, but got Failure: ${result.msg}")
                }
            }
        }

        // Call the function to add the favorite
        viewModel.addToFavorite(product)

        // Advance until idle to process emissions
        advanceUntilIdle()

        // Ensure the job is canceled after processing
        job.join() // wait for cancellation to complete
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
