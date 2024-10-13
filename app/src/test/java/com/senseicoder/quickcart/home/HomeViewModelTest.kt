package com.senseicoder.quickcart.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.senseicoder.quickcart.category.FakeProductsRepo
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.repos.product.ProductsRepoInterface
import com.senseicoder.quickcart.features.main.ui.home.viewmodel.HomeViewModel
import com.senseicoder.quickcart.core.wrappers.ApiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private val fakeRepo = FakeProductsRepo()

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = HomeViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getBrand should retrieve all brands successfully`() = runBlockingTest {
        // Call the function to retrieve brands
        viewModel.getBrand()

        // Get the brands state using first to get immediate value from flow
        val state = viewModel.brands.first()

        // Assert that the brands received are equal to the sample brands in the fake repository
        if (state is ApiState.Success) {
            val expectedBrands = fakeRepo.sampleBrands
            assertEquals(expectedBrands, state.data)
        } else {
            throw AssertionError("Expected success state but got: $state")
        }
    }

    @Test
    fun `getBrand should handle error correctly`() = runBlockingTest {
        // Create a subclass that simulates an error scenario
        val errorRepo = object : FakeProductsRepo() {
            // Overriding getAllBrand to throw an exception
            override suspend fun getAllBrand(): Flow<List<DisplayBrand>> = flow {
                throw Exception("Test Error")
            }
        }

        // Initialize the HomeViewModel with the error repository
        viewModel = HomeViewModel(errorRepo)

        // Call the function to retrieve brands
        viewModel.getBrand()

        // Get the brands state
        val state = viewModel.brands.first()

        // Assert that the state is a failure with the expected error message
        if (state is ApiState.Failure) {
            assertTrue(state.msg.contains("Test Error")) // Check if the error message is as expected
        } else {
            throw AssertionError("Expected failure state but got: $state")
        }
    }

}
