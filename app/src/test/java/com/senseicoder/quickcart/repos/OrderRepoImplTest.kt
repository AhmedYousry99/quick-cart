package com.senseicoder.quickcart.repos

import FakeStorefrontHandler
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.repos.order.OrderRepoImpl
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Test

class OrderRepoImplTest {

    private val fakeStorefrontHandler = FakeStorefrontHandler()
    private val fakeSharedPrefs = FakeSharedPrefs()
    private val orderRepo = OrderRepoImpl(fakeStorefrontHandler, fakeSharedPrefs)

    @Test
    fun `test getCustomerOrders returns correct orders`() = runBlocking {
        val token = "fake_token"
        val result = orderRepo.getCustomerOrders(token).toList()

        // Check if the loading state is emitted first
        assertEquals(ApiState.Loading, result[0])

        // Check if the success state is emitted and contains the correct data
        val successState = result[1] as ApiState.Success
        assertEquals(2, successState.data.size)

        // Validate properties of the first order
        assertEquals("1", successState.data[0].id)
        assertEquals("Order 1", successState.data[0].name)
        assertEquals("200.00", successState.data[0].totalPriceAmount)
        assertEquals("USD", successState.data[0].totalPriceCurrencyCode)
        assertEquals("123 Main St", successState.data[0].address?.address1)
        assertEquals("Springfield", successState.data[0].address?.city)

        // Validate properties of the second order
        assertEquals("2", successState.data[1].id)
        assertEquals("Order 2", successState.data[1].name)
        assertEquals("150.00", successState.data[1].totalPriceAmount)
        assertEquals("USD", successState.data[1].totalPriceCurrencyCode)
        assertEquals("456 Elm St", successState.data[1].address?.address1)
        assertEquals("Shelbyville", successState.data[1].address?.city)
    }

    @Test
    fun `test readUserToken returns correct token`() = runBlocking {
        // Set a token in the fake shared preferences
        val expectedToken = "fake_user_token"
        fakeSharedPrefs.setSharedPrefString(Constants.USER_TOKEN, expectedToken)

        // Call the readUserToken function
        val actualToken = orderRepo.readUserToken()

        // Assert that the token retrieved from shared preferences is as expected
        assertEquals(expectedToken, actualToken)
    }

    @Test
    fun `test readUserToken returns default token when no token is set`() = runBlocking {
        // Call the readUserToken function without setting a token
        val actualToken = orderRepo.readUserToken()

        // Assert that the default token is returned
        assertEquals(Constants.USER_TOKEN_DEFAULT, actualToken)
    }
}
