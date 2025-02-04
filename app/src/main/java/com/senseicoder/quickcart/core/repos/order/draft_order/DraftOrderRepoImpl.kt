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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.timeout
import retrofit2.Response
import kotlin.math.log
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class DraftOrderRepoImpl(val orderRemoteDataSource: OrderRemoteDataSource,): DraftOrderRepo  {

    companion object{
        private const val  TAG = "DRAFT ORDER REPO"
    }
    private val token = SharedPrefsService.getSharedPrefString(Constants.USER_TOKEN, Constants.USER_TOKEN_DEFAULT )
    override suspend fun createDraftOrder(request: DraftOrderReqRes): Flow<Response<DraftOrderReqRes>> {
        return flow{
            val res = orderRemoteDataSource.createDraftOrder(request)

            if (res.isSuccessful) {
                emit(res)
                Log.d(TAG, "createDraftOrder: ${request.draft_order}")
            }
            else
                throw  Exception(res.message())
        }
    }

    override suspend fun completeDraftOrder(draftOrderId: Long): Flow<Response<DraftOrderReqRes>> {
        return flow{
            val res = orderRemoteDataSource.completeDraftOrder(draftOrderId)
            if (res.isSuccessful)
                emit(res)
            else {
                if(res.message() != Constants.Errors.DraftOrderComplete.MEMBER_CHECKING_WAIT)
                    throw Exception(res.message())
            }
        }.retryWhen{
            cause, attempt ->
            Log.d(TAG, "completeDraftOrder retrying: $cause, ${cause.message}")
            delay(2000)
            attempt < 5
        }.timeout(15.seconds)
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
        }.timeout(15.seconds)
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