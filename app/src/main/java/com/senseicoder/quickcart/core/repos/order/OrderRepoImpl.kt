package com.senseicoder.quickcart.core.repos.order

import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.senseicoder.quickcart.core.services.SharedPrefs
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.Flow

class OrderRepoImpl (
    private val shopifyRemoteDataSource: StorefrontHandler,
    private val sharedPreferences : SharedPrefs
) : OrderRepo {

    override fun getCustomerOrders(token: String): Flow<ApiState<List<Order>>> {
        return shopifyRemoteDataSource.getCustomerOrders(token)
    }



    override suspend fun readUserToken(): String {
       return sharedPreferences.getSharedPrefString(Constants.USER_TOKEN,Constants.USER_TOKEN_DEFAULT)
    }

}