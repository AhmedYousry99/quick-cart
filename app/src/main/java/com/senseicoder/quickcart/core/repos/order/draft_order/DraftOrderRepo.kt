package com.senseicoder.quickcart.core.repos.order.draft_order

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.DraftOrderReqRes
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.storefront.CustomerAddressesQuery
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface DraftOrderRepo {

    suspend fun createDraftOrder(request: DraftOrderReqRes): Flow<Response<DraftOrderReqRes>>

    suspend fun completeDraftOrder(draftOrderId: Long): Flow<Response<DraftOrderReqRes>>

    suspend fun sendInvoice(draftOrderId: Long): Flow<Response<DraftOrderReqRes>>

    suspend fun getCustomerAddresses(): Flow<CustomerAddressesQuery.Customer?>


}