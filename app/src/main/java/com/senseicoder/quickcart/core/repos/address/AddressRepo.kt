package com.senseicoder.quickcart.core.repos.address

import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.flow.Flow

interface AddressRepo {
    suspend fun getCustomerAddresses(): Flow<CustomerAddressesQuery.Customer?>

    suspend fun updateCustomerAddress(address : MailingAddressInput, id :String,): Flow<String?>

    suspend fun deleteAddress(id :String): Flow<String?>

    suspend fun createAddress(
        customerAddress: MailingAddressInput
    ): Flow<String?>

    suspend fun updateDefaultAddress(id :String): Flow<CustomerDefaultAddressUpdateMutation.CustomerDefaultAddressUpdate>?

    fun updateToken()
}