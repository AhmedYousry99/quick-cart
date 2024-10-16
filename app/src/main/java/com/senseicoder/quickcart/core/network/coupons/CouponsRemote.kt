package com.senseicoder.quickcart.core.network.coupons

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import retrofit2.Response

interface CouponsRemote {
    suspend fun checkCouponDetails(couponId: String): Response<DiscountCodesResponse>
    suspend fun fetchCoupons(): Response<PriceRulesResponse>

}