package com.senseicoder.quickcart.core.repos.coupons

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.PriceRulesResponse

interface CouponsRepo {

    suspend fun checkCouponDetails(couponId: String): DiscountCodesResponse
    suspend fun fetchCoupons(): PriceRulesResponse
}
