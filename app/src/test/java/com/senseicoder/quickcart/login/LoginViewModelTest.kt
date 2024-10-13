package com.senseicoder.quickcart.login

import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.login.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeCustomerRepo: FakeCustomerRepo

    @Before
    fun setup() {
        fakeCustomerRepo = FakeCustomerRepo()
        viewModel = LoginViewModel(fakeCustomerRepo)
    }

    @Test
    fun `loginUsingNormalEmail should emit Success state when login is successful`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password"

        // Act: Call the login function
        viewModel.loginUsingNormalEmail(email, password)

        // Collect the login state in a separate coroutine
        val job = launch {
            viewModel.loginState.collect { result ->
                // Assert: Expecting loading state initially
                if (result is ApiState.Loading) {
                    // Assert that we received the loading state
                    assertEquals(ApiState.Loading, result)
                }
                // Assert: Expecting success state after processing
                if (result is ApiState.Success) {
                    val customerData = result.data
                    assertEquals("fake_user_id", customerData.id)
                    assertEquals("fake_user_token", customerData.token)
                    assertEquals(email, customerData.email)
                    assertEquals("Fake User", customerData.displayName)
                    assertEquals("fake_cart_id", customerData.cartId)
                }
            }
        }

        // Advance until idle to process emissions
        advanceUntilIdle()

        // Cancel the job to stop collecting
        job.cancel()
    }

    @Test
    fun `loginUsingNormalEmail should emit Failure state when login fails`() = runTest {
        // Arrange
        fakeCustomerRepo.shouldReturnError = true
        val email = "test@example.com"
        val password = "password"

        // Act: Call the login function
        viewModel.loginUsingNormalEmail(email, password)

        // Advance until idle to process emissions
        advanceUntilIdle()

        // Final assertion to check if the state is as expected
        val finalResult = viewModel.loginState.first()
        assert(finalResult is ApiState.Failure)
        val errorMessage = (finalResult as ApiState.Failure).msg
        assertEquals("Login failed", errorMessage)
    }

//    @Test
//    fun `signupAsGuest should emit Success state when signup is successful`() = runTest {
//        // Arrange
//        val fakeCustomerRepo = FakeCustomerRepo()
//        val viewModel = LoginViewModel(fakeCustomerRepo)
//
//        // Act: Call the signup function
//        viewModel.signupAsGuest()
//
//        // Collect the login state in a separate coroutine
//        val job = launch {
//            viewModel.loginState.collect { result ->
//                // Assert: Expecting loading state initially
//                if (result is ApiState.Loading) {
//                    assertEquals(ApiState.Loading, result)
//                }
//                // Assert: Expecting success state after processing
//                if (result is ApiState.Success) {
//                    val customerData = result.data
//                    assertEquals("fake_guest_user_id", customerData.id)
//                    assertEquals("fake_guest_user_token", customerData.token)
//                    assertEquals("guest@example.com", customerData.email)
//                    assertEquals("Guest User", customerData.displayName)
//                    assertEquals("fake_guest_cart_id", customerData.cartId)
//                }
//            }
//        }
//
//        // Advance until idle to process emissions
//        advanceUntilIdle()
//
//        // Cancel the job to stop collecting
//        job.cancel()
//    }

}
