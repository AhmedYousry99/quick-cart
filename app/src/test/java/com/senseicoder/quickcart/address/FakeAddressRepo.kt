import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.repos.address.AddressRepo
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FakeAddressRepo : AddressRepo {

    var shouldReturnError = false

    override suspend fun getCustomerAddresses(): Flow<CustomerAddressesQuery.Customer?> {
        return flow {
            if (shouldReturnError) {
                emit(null)
            } else {
                // Create mock addresses
                val mockAddresses = CustomerAddressesQuery.Addresses(
                    edges = listOf(
                        CustomerAddressesQuery.Edge(
                            node = CustomerAddressesQuery.Node(
                                address1 = "123 Main St",
                                address2 = "Apt 4B",
                                city = "Anytown",
                                country = "USA",
                                firstName = "John",
                                id = "1",
                                lastName = "Doe",
                                latitude = 37.7749,
                                longitude = -122.4194,
                                phone = "+11234567890",
                                name = "John Doe",
                                provinceCode = "CA",
                                province = "California",
                                zip = "94103",
                                countryCodeV2 = null, // Set this if you have a CountryCode value
                                company = "Doe Industries"
                            )
                        ),
                        CustomerAddressesQuery.Edge(
                            node = CustomerAddressesQuery.Node(
                                address1 = "456 Side St",
                                address2 = null,
                                city = "Othertown",
                                country = "USA",
                                firstName = "Jane",
                                id = "2",
                                lastName = "Doe",
                                latitude = null,
                                longitude = null,
                                phone = "+19876543210",
                                name = "Jane Doe",
                                provinceCode = null,
                                province = null,
                                zip = "94043",
                                countryCodeV2 = null, // Set this if you have a CountryCode value
                                company = null
                            )
                        )
                    )
                )

                // Creating the Customer object
                val customer = CustomerAddressesQuery.Customer(
                    addresses = mockAddresses,
                    defaultAddress = null
                )

                // Emit the data wrapped in the Data class
                    emit (customer)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun updateCustomerAddress(
        address: MailingAddressInput,
        id: String
    ): Flow<String?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAddress(id: String): Flow<String?> {
        TODO("Not yet implemented")
    }

    override suspend fun createAddress(customerAddress: MailingAddressInput): Flow<String?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDefaultAddress(id: String): Flow<CustomerDefaultAddressUpdateMutation.CustomerDefaultAddressUpdate>? {
        TODO("Not yet implemented")
    }

    override fun updateToken() {
        TODO("Not yet implemented")
    }

    // Implement other methods if needed
}
