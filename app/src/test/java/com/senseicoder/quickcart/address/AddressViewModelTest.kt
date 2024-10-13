package com.senseicoder.quickcart.address

import FakeAddressRepo
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.features.main.ui.address.viewmodel.AddressViewModel
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddressViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeAddressRepo: FakeAddressRepo
    private lateinit var viewModel: AddressViewModel
    private lateinit var testDispatcher: TestCoroutineDispatcher

    @Before
    fun setUp() {
        fakeAddressRepo = FakeAddressRepo()
        testDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testDispatcher) // Set Main dispatcher to testDispatcher
        viewModel = AddressViewModel(fakeAddressRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the Main dispatcher
        testDispatcher.cleanupTestCoroutines() // Clean up the test dispatcher
    }

    @Test
    fun `createAddress should emit success when repository returns success`() =
        testDispatcher.runBlockingTest {
            // Arrange
            val mailingAddressInput = MailingAddressInput(/* Initialize with appropriate values */)
            fakeAddressRepo.shouldReturnError = false // Make sure to return success

            // Act
            viewModel.createAddress(mailingAddressInput)

            // Assert
            val state = viewModel.createdAddress.first() // Collect the first emitted value
            when (state) {
                is ApiState.Success -> {
                    assert(state.data == "Address created successfully")
                }
                is ApiState.Failure -> {
                    assert(false) // This should not happen
                }
                else -> {}
            }
        }

    @Test
    fun `createAddress should emit failure when repository returns an error`() =
        testDispatcher.runBlockingTest {
            // Arrange
            val mailingAddressInput = MailingAddressInput(/* Initialize with appropriate values */)
            fakeAddressRepo.shouldReturnError = true // Make sure to return an error

            // Act
            viewModel.createAddress(mailingAddressInput)

            // Assert
            val state = viewModel.createdAddress.first() // Collect the first emitted value
            when (state) {
                is ApiState.Failure -> {
                    assert(state.msg == "Error creating address")
                }
                is ApiState.Success -> {
                    assert(false) // This should not happen
                }
                else -> {}
            }
        }
}
