package com.senseicoder.quickcart.main

import com.senseicoder.quickcart.core.repos.address.AddressRepo
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeAddressRepo : AddressRepo {
    private var shouldReturnError = false
    private var customerAddresses: CustomerAddressesQuery.Customer? = null

    // Method to set the mock addresses
    fun setCustomerAddresses(value: CustomerAddressesQuery.Customer?) {
        customerAddresses = value
    }

    // Method to toggle error simulation
    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getCustomerAddresses(): Flow<CustomerAddressesQuery.Customer?> = flow {
        if (shouldReturnError) {
            throw Exception("Test Exception")
        }
        emit(customerAddresses)
    }

    // Implement other methods as needed for your tests
    override suspend fun updateCustomerAddress(address: MailingAddressInput, id: String): Flow<String?> = flow { emit(null) }
    override suspend fun deleteAddress(id: String): Flow<String?> = flow { emit(null) }
    override suspend fun createAddress(customerAddress: MailingAddressInput): Flow<String?> = flow { emit(null) }
    override suspend fun updateDefaultAddress(id: String): Flow<CustomerDefaultAddressUpdateMutation.CustomerDefaultAddressUpdate>? = null
    override fun updateToken() {}
}

