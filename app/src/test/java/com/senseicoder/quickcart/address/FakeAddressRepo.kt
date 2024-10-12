import com.senseicoder.quickcart.core.repos.address.AddressRepo
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAddressRepo : AddressRepo {

    var shouldReturnError = false

    override suspend fun getCustomerAddresses(): Flow<CustomerAddressesQuery.Customer?> {
        // Implement if needed for other tests
        return flow { emit(null) }
    }

    override suspend fun updateCustomerAddress(address: MailingAddressInput, id: String): Flow<String?> {
        // Implement if needed for other tests
        return flow { emit("Address updated successfully") }
    }

    override suspend fun deleteAddress(id: String): Flow<String?> {
        // Implement if needed for other tests
        return flow { emit("Address deleted successfully") }
    }

    override suspend fun createAddress(customerAddress: MailingAddressInput): Flow<String?> {
        return if (shouldReturnError) {
            flow { throw Exception("Error creating address") }
        } else {
            flow { emit("Address created successfully") }
        }
    }

    override suspend fun updateDefaultAddress(id: String): Flow<CustomerDefaultAddressUpdateMutation.CustomerDefaultAddressUpdate>? {
        // Implement if needed for other tests
        return null
    }

    override fun updateToken() {
        // No-op for this test
    }
}
