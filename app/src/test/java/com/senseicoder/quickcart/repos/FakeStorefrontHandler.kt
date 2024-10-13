import com.senseicoder.quickcart.core.entity.order.Address
import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.storefront.CartLinesUpdateMutation
import com.storefront.CreateCustomerAccessTokenMutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import com.storefront.type.MailingAddressInput

class FakeStorefrontHandler : StorefrontHandler {
    private val fakeOrders = listOf(
        Order(
            id = "1",
            name = "Order 1",
            address = Address(address1 = "123 Main St", address2 = "Apt 4B", city = "Springfield", country = "USA"),
            totalPriceAmount = "200.00",
            totalPriceCurrencyCode = "USD",
            subTotalPriceAmount = "180.00",
            subTotalPriceCurrencyCode = "USD",
            totalTaxAmount = "20.00",
            totalTaxCurrencyCode = "USD",
            processedAt = "2024-10-12T12:00:00Z",
            phone = "123-456-7890",
            products = listOf() // You can add sample products here if needed
        ),
        Order(
            id = "2",
            name = "Order 2",
            address = Address(address1 = "456 Elm St", address2 = null, city = "Shelbyville", country = "USA"),
            totalPriceAmount = "150.00",
            totalPriceCurrencyCode = "USD",
            subTotalPriceAmount = "140.00",
            subTotalPriceCurrencyCode = "USD",
            totalTaxAmount = "10.00",
            totalTaxCurrencyCode = "USD",
            processedAt = "2024-10-13T12:00:00Z",
            phone = null,
            products = listOf() // You can add sample products here if needed
        )
    )


    override fun getCustomerOrders(token: String): Flow<ApiState<List<Order>>> {
        return flow {
            emit(ApiState.Loading) // Emit loading state first
            delay(100) // Simulate network delay
            emit(ApiState.Success(fakeOrders)) // Emit success with fake orders
        }
    }

    // Other methods can be left unimplemented or as stubs
    override suspend fun loginUser(email: String, password: String) = TODO()
    override fun createCustomer(email: String, password: String, firstName: String, lastName: String) = TODO()
    override suspend fun removeProductFromCart(cartId: String, lineId: String) = TODO()
    override suspend fun getProductsCart(cartId: String) = TODO()
    override suspend fun updateQuantityOfProduct(
        cartId: String,
        lineId: String,
        quantity: Int
    ): Flow<CartLinesUpdateMutation.Lines?> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductDetailsById(id: String) = TODO()
    override suspend fun getCustomerAddresses(token: String) = TODO()
    override suspend fun updateCustomerAddress(address: MailingAddressInput, token: String, id: String) = TODO()
    override suspend fun deleteAddress(id: String, token: String) = TODO()
    override suspend fun createAddress(customerAddress: MailingAddressInput, token: String) = TODO()
    override suspend fun updateDefaultAddress(token: String, id: String) = TODO()
    override suspend fun createCart(email: String) = TODO()
    override suspend fun addToCartById(cartId: String, quantity: Int, variantId: String) = TODO()
    override suspend fun getProductsByQuery(query: String) = TODO()
}
