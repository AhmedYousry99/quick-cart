package com.senseicoder.quickcart.core.network.order

import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderInterface {
    companion object {
        private const val ACCESS_TOKEN = BuildConfig.shopify_admin_api_access_token
        private const val SECTION_ONE_HEADER = "Content-Type: application/json"
        private const val SECTION_TWO_HEADER = "X-Shopify-Access-Token: ${ACCESS_TOKEN}"
    }

    @Headers(SECTION_ONE_HEADER, SECTION_TWO_HEADER)
    @POST("draft_orders.json")

    suspend fun createDraftOrder(
        @Body request: DraftOrderReqRes
    ): Response<DraftOrderReqRes>


    @Headers(SECTION_ONE_HEADER, SECTION_TWO_HEADER)
    @PUT("draft_orders/{draftOrderId}/complete.json")
    suspend fun completeDraftOrder(
        @Path("draftOrderId") draftOrderId: Long,
    ): Response<DraftOrderReqRes>



    @Headers(SECTION_ONE_HEADER, SECTION_TWO_HEADER)
    @POST("draft_orders/{draftOrderId}/send_invoice.json")

    suspend fun sendInvoice(
        @Path("draftOrderId") draftOrderId: Long
    ): Response<DraftOrderReqRes>


}