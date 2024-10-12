package com.senseicoder.quickcart.features.main.ui.category.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.senseicoder.quickcart.category.FakeProductsRepo
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.core.wrappers.ApiState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CategoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CategoryViewModel
    private val fakeRepo = FakeProductsRepo()

    @Before
    fun setUp() {
        // Set the main dispatcher to a TestDispatcher
       Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = CategoryViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
       Dispatchers.resetMain()
    }

    @Test
    fun `getProducts should retrieve all products`() = runBlockingTest {
        // First call to getProducts will initialize the products state
        viewModel.getProducts()

        // Get the products state using first to get immadiate value from flow
        val state = viewModel.products.first()

        // Assert that the products received are equal to the sample products
        if (state is ApiState.Success) {
            assertEquals(fakeRepo.getAllProduct().first(), state.data)
        }
    }



    @Test
    fun `filterByMainCategory filters correctly when filterMainCategory is true`() = runBlockingTest {
        // Initialize the Fake Repository
        val fakeRepo = FakeProductsRepo()
        val viewModel = CategoryViewModel(fakeRepo)

        // Trigger the filtering
        viewModel.filterMainCategory = true // Set the flag to true
        viewModel.allData = fakeRepo.sampleProducts // Directly set allData from the Fake Repository

        // Call the function with the main category
        viewModel.filterByMainCategory("SHOES")

        advanceUntilIdle() // Wait for all coroutines to complete

        // Observe the result
        val state = viewModel.products.value
        if (state is ApiState.Success) {
            val expectedProducts = listOf(
                DisplayProduct(1, "Shoe 1", "SHOES", "tag1,tag2", "description", "100.00"),
                DisplayProduct(2, "Shoe 2", "SHOES", "tag1,tag2", "description", "150.00")
            )
            assertEquals(expectedProducts, state.data) // Assert the result
        } else {
            throw AssertionError("Expected success state after filtering")
        }
    }



}
