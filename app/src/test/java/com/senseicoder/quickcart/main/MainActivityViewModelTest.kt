package com.senseicoder.quickcart.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.Currency
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.model.Meta
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.testing.FakeCurrencyRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel
    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var fakeCurrencyRepo: FakeCurrencyRepo

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        fakeCurrencyRepo = FakeCurrencyRepo()
        viewModel = MainActivityViewModel(fakeCurrencyRepo)

        // Set the Main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `getCurrencyRate should emit success state on valid response`() = testDispatcher.runBlockingTest {
        // Arrange
        val newCurrency = "USD"
        val expectedData = CurrencyResponse(
            data = mapOf("USD" to Currency(code = "USD", value = 1.0)),
            meta = Meta(last_updated_at = "2024-10-12T00:00:00Z")
        )

        // Set the mock response
        fakeCurrencyRepo.currencyResponse = expectedData

        // Act
        viewModel.getCurrencyRate(newCurrency)

        // Collect from the SharedFlow in a coroutine
        val job = launch {
            viewModel.currency.collect { state ->
                when (state) {
                    is ApiState.Success -> {
                        assertEquals(expectedData, state.data)
                    }
                    is ApiState.Failure -> {
                        // This should not be called in this case
                        assert(false) { "Expected success, but got failure: ${state.msg}" }
                    }
                    else -> {}
                }
            }
        }

        // Clean up
        job.cancel() // Ensure the coroutine is cancelled after the test
    }

    @Test
    fun `getCurrencyRate should emit failure state on error response`() = testDispatcher.runBlockingTest {
        // Arrange
        val newCurrency = "USD"
        fakeCurrencyRepo.shouldReturnError = true // Set to return an error

        // Act
        viewModel.getCurrencyRate(newCurrency)

        // Collect from the SharedFlow in a coroutine
        val job = launch {
            viewModel.currency.collect { state ->
                when (state) {
                    is ApiState.Failure -> {
                        assertEquals("error", state.msg) // Assuming "error" is the message returned in the Meta
                    }
                    is ApiState.Success -> {
                        // This should not be called in this case
                        assert(false) { "Expected failure, but got success: ${state.data}" }
                    }
                    else -> {}
                }
            }
        }

        // Clean up
        job.cancel() // Ensure the coroutine is cancelled after the test
    }
}
