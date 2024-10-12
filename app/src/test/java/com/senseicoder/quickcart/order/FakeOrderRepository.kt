package com.senseicoder.quickcart.order

import com.senseicoder.quickcart.core.entity.order.Address
import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.core.repos.order.OrderRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeOrderRepository : OrderRepo {
    private var shouldReturnError = false

    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun getCustomerOrders(token: String): Flow<ApiState<List<Order>>> = flow {

        // Simulate a network call that returns dummy orders
        if (shouldReturnError) {
            throw Exception("Fake error")
        } else {
            val dummyOrders = listOf(
                Order(
                    "1",
                    "Order 1",
                    Address("123 Main St", "City", "State", "12345"), // Replace with actual Address object
                    "100.00",
                    "USD",
                    "90.00",
                    "USD",
                    "10.00",
                    "USD",
                    "2023-01-01",
                    "1234567890",
                    listOf()
                ),
                Order(
                    "2",
                    "Order 2",
                    Address("456 Another St", "Another City", "Another State", "54321"),
                    "150.00",
                    "USD",
                    "140.00",
                    "USD",
                    "10.00",
                    "USD",
                    "2023-02-01",
                    "0987654321",
                    listOf()
                )
            )
            emit(ApiState.Success(dummyOrders))
        }
    }

    override suspend fun readUserToken(): String {
        return "dummy_token"
    }
}
