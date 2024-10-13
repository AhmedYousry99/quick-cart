import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.senseicoder.quickcart.category.FakeProductsRepo
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.main.ui.search.viewmodel.SearchViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SearchViewModel
    private lateinit var fakeRepo: FakeProductsRepo

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeProductsRepo()
        viewModel = SearchViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchProducts should emit loading and success state`() = runTest {
        // Set up a query for testing
        val query = "Shoe"

        // Call the searchProducts function
        viewModel.searchProducts(query)

        // Collect emissions from _searchResults
        viewModel.searchResults.test {
            // Assert that the first emission is the Loading state
            assertEquals(ApiState.Loading, awaitItem())

            // Advance the coroutine until all tasks are complete
            testScheduler.advanceUntilIdle()

            // Assert that the next emission is the Success state with the correct data
            val successState = awaitItem() as ApiState.Success<List<ProductDTO>>
            assertEquals(2, successState.data.size) // Expecting 2 products containing "Shoe"

            // Verify the content of the products in the success state
            assertEquals("Shoe 1", successState.data[0].title)
            assertEquals("Shoe 2", successState.data[1].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

}
