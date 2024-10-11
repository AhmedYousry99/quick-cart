package com.senseicoder.quickcart.core.network.coupons

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface CouponsRemote {
    suspend fun checkCouponDetails(couponId: String): Flow<Response<DiscountCodesResponse>>
    suspend fun fetchCoupons(): Flow<Response<PriceRulesResponse>>

}