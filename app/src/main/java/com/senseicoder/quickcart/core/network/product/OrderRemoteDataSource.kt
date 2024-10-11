package com.senseicoder.quickcart.core.network.product

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import retrofit2.Response

interface OrderRemoteDataSource {

    suspend fun createDraftOrder(request: DraftOrderReqRes): Response<DraftOrderReqRes>

    suspend fun completeDraftOrder(draftOrderId: Long): Response<DraftOrderReqRes>

    suspend fun sendInvoice(draftOrderId: Long): Response<DraftOrderReqRes>

}