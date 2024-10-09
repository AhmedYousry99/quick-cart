package com.senseicoder.quickcart

import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.main.ui.order.viewmodel.OrderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.senseicoder.quickcart.FakeOrderRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class OrderViewModelTest {

    private lateinit var viewModel: OrderViewModel
    private lateinit var fakeRepository: FakeOrderRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeOrderRepository()
        viewModel = OrderViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCustomerOrders should return orders successfully`() = runTest {
        viewModel.getCustomerOrders(fakeRepository.readUserToken())
        val result = viewModel.apiState.first()
        assertTrue(result is ApiState.Success)
        assertEquals(2, (result as ApiState.Success).data.size)
    }

    @Test
    fun `getCustomerOrders should handle failure`() = runTest {
        fakeRepository.setShouldReturnError(true)
        viewModel.getCustomerOrders(fakeRepository.readUserToken())
        val result = viewModel.apiState.first()
        assertTrue(result is ApiState.Failure)
        assertEquals("Failed to fetch customer orders: Fake error", (result as ApiState.Failure).msg)
    }
}
