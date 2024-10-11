package com.senseicoder.quickcart.core.repos.coupons

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface CouponsRepo {

    suspend fun checkCouponDetails(couponId: String): Flow<DiscountCodesResponse>
    suspend fun fetchCoupons(): Flow<PriceRulesResponse>
}
