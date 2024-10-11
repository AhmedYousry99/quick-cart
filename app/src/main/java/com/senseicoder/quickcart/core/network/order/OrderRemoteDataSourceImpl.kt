package com.senseicoder.quickcart.core.network.order

import android.util.Log
import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.senseicoder.quickcart.core.network.ApiService
import com.senseicoder.quickcart.core.network.product.OrderRemoteDataSource
import retrofit2.Response

object OrderRemoteDataSourceImpl : OrderRemoteDataSource{
private val TAG = "OrderRemoteDataSourceImpl"
    private val orderInterface: OrderInterface = ApiService.orderApiService
    override suspend fun createDraftOrder(request: DraftOrderReqRes): Response<DraftOrderReqRes> {
        return orderInterface.createDraftOrder(request)
    }

    override suspend fun completeDraftOrder(draftOrderId: Long): Response<DraftOrderReqRes> {
        val res = orderInterface.completeDraftOrder(draftOrderId)
        Log.d(TAG, "completeDraftOrder: ${draftOrderId}): ${res}")
        return res
    }

    override suspend fun sendInvoice(draftOrderId: Long): Response<DraftOrderReqRes> {
        return orderInterface.sendInvoice(draftOrderId)
    }



}