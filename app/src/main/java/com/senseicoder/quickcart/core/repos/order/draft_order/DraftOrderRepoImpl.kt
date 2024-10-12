package com.senseicoder.quickcart.core.repos.order.draft_order

import android.util.Log
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.product.OrderRemoteDataSource
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.storefront.CustomerAddressesQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import kotlin.math.log

class DraftOrderRepoImpl(val orderRemoteDataSource: OrderRemoteDataSource,): DraftOrderRepo  {

    companion object{
        private const val  TAG = "DRAFT ORDER REPO"
    }
    private val token = SharedPrefsService.getSharedPrefString(Constants.USER_TOKEN, Constants.USER_TOKEN_DEFAULT )
    override suspend fun createDraftOrder(request: DraftOrderReqRes): Flow<Response<DraftOrderReqRes>> {
        return flow{
            val res = orderRemoteDataSource.createDraftOrder(request)
            if (res.isSuccessful)
                emit(res)
            else
                throw  Exception(res.message())
        }
    }

    override suspend fun completeDraftOrder(draftOrderId: Long): Flow<Response<DraftOrderReqRes>> {
        return flow{
            val res = orderRemoteDataSource.completeDraftOrder(draftOrderId)
            if (res.isSuccessful)
                emit(res)
            else
                throw  Exception(res.message())
        }
    }

    override suspend fun sendInvoice(draftOrderId: Long): Flow<DraftOrderReqRes?> {
        return flow{
            val res = orderRemoteDataSource.sendInvoice(draftOrderId)
            Log.d(TAG, "sendInvoice: ${res.errorBody().toString()} \n ${res.body()}")

            if (res.isSuccessful)
                emit(res.body())
            else {
                throw Exception(res.message())
            }

        }
    }

    override suspend fun getCustomerAddresses(): Flow<CustomerAddressesQuery.Customer?> {
        return flow {
            StorefrontHandlerImpl.getCustomerAddresses(token).catch {
                emit(null)
            }.collect {
                emit(it)
            }
        }
    }

}