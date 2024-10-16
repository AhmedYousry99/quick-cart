import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.Currency
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.model.Meta
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepo
import com.senseicoder.quickcart.core.repos.address.AddressRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.testing.FakeCurrencyRepo
import com.storefront.CustomerAddressesQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class MainActivityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeCurrencyRepo: FakeCurrencyRepo
    private lateinit var fakeAddressRepo: FakeAddressRepo
    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeCurrencyRepo = FakeCurrencyRepo()
        fakeAddressRepo = FakeAddressRepo()
        viewModel = MainActivityViewModel(currencyRepo = fakeCurrencyRepo, addressRepo = fakeAddressRepo)

        // Suppress Android logging
        System.setProperty("robolectric.logging", "none") // Ignore logging during test
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCurrencyRate emits Loading then Success`() = runTest {
        // Set up the expected response
        val expectedCurrencyResponse = CurrencyResponse(
            data = mapOf("USD" to Currency("USD", 1.0)),
            meta = Meta(last_updated_at = "2024-10-16T12:00:00Z")
        )

        // Set up the fake repository to return success
        fakeCurrencyRepo = FakeCurrencyRepo(expectedCurrencyResponse)
        viewModel = MainActivityViewModel(currencyRepo = fakeCurrencyRepo, addressRepo = fakeAddressRepo)

        // Collect state emissions
        val states = mutableListOf<ApiState<CurrencyResponse>>()
        val job = launch {
            viewModel.currency.collect { state -> states.add(state) }
        }

        // Trigger the getCurrencyRate method
        viewModel.getCurrencyRate("USD")

        // Allow emissions to be processed
        advanceUntilIdle()

        // Check if states were emitted as expected
        assertTrue("Expected at least one state to be emitted", states.isNotEmpty())

        // Check if the first state is Loading
        assertTrue("Expected first state to be Loading", states.first() is ApiState.Loading)

        // Print all emitted states for debugging
        states.forEachIndexed { index, state -> println("State $index: $state") }

        // Check if the last state is Success
        val lastState = states.last()
        assertTrue("Expected last state to be Success, but got $lastState", lastState is ApiState.Success)

        // Verify the content of the Success state
        assertEquals("Expected response to match", expectedCurrencyResponse, (lastState as ApiState.Success).data)

        job.cancel() // Cancel the collection job
    }

    @Test
    fun `getCurrencyRate emits Loading then Error`() = runTest {
        // Set up the fake repository to simulate an error
        val errorRepo = FakeCurrencyRepo(shouldReturnError = true)
        viewModel = MainActivityViewModel(currencyRepo = errorRepo, addressRepo = fakeAddressRepo)

        // Collect state emissions
        val states = mutableListOf<ApiState<CurrencyResponse>>()
        val job = launch {
            viewModel.currency.collect { state -> states.add(state) }
        }

        // Trigger the getCurrencyRate method
        viewModel.getCurrencyRate("USD")

        // Allow emissions to be processed
        advanceUntilIdle()

        // Check if states were emitted as expected
        assertTrue("Expected at least one state to be emitted", states.isNotEmpty())

        // Check if the first state is Loading
        assertTrue("Expected first state to be Loading", states.first() is ApiState.Loading)

        // Print all emitted states for debugging
        states.forEachIndexed { index, state -> println("State $index: $state") }

        // Check if the last state is an Error
        val lastState = states.last()
        assertTrue("Expected last state to be Error, but got $lastState", lastState is ApiState.Failure)

        job.cancel() // Cancel the collection job
    }

    @Test
    fun `getCustomerAddresses emits customer addresses successfully`() = runTest {
        // Ensure there's no error in the fake address repository
        fakeAddressRepo.shouldReturnError = false

        // Set up mock data in FakeAddressRepo
        val customerAddressesFlow = fakeAddressRepo.getCustomerAddresses()

        // Collect the customer addresses emitted
        val addresses = mutableListOf<CustomerAddressesQuery.Customer?>()
        customerAddressesFlow.collect { address -> addresses.add(address) }

        // Check that we received at least one emission
        assertTrue("Expected at least one customer address to be emitted", addresses.isNotEmpty())

        // Verify the content of the emitted addresses
        val emittedCustomer = addresses.first()
        assertNotNull("Expected emitted customer to be not null", emittedCustomer)

        // Further assertions can be added here, like checking address fields
    }



    @Test
    fun `getCustomerAddresses emits null when there is an error`() = runTest {
        // Simulate an error in the fake address repository
        fakeAddressRepo.shouldReturnError = true

        // Set up mock data in FakeAddressRepo
        val customerAddressesFlow = fakeAddressRepo.getCustomerAddresses()

        // Collect the customer addresses emitted directly
        val addresses = mutableListOf<CustomerAddressesQuery.Customer?>()
        customerAddressesFlow.collect { address ->
            addresses.add(address)
        }

        // Check that we received exactly one emission
        assertEquals("Expected exactly one customer address emission", 1, addresses.size)

        // Verify the content of the emitted address
        val emittedCustomer = addresses.first()
        assertNull("Expected emitted customer to be null when there's an error", emittedCustomer)
    }


}