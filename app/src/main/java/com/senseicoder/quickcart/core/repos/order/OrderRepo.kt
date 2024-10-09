package com.senseicoder.quickcart.core.repos.order

import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.Flow


interface OrderRepo {
    fun getCustomerOrders(token: String): Flow<ApiState<List<Order>>>


    suspend fun readUserToken(): String
}