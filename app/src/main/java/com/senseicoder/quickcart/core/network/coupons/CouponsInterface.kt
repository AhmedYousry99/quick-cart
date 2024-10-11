package com.senseicoder.quickcart.core.network.coupons

import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface CouponsInterface {
    companion object {
        private const val ACCESS_TOKEN = BuildConfig.shopify_admin_api_access_token
        private const val SECTION_ONE_HEADER = "Content-Type: application/json"
        private const val SECTION_TWO_HEADER = "X-Shopify-Access-Token: ${ACCESS_TOKEN}"
    }
    @Headers(SECTION_ONE_HEADER, SECTION_TWO_HEADER)

    @GET("price_rules.json")
    suspend fun getCoupons(): Response<PriceRulesResponse>


    @Headers(SECTION_ONE_HEADER, SECTION_TWO_HEADER)
    @GET("price_rules/{couponId}/discount_codes.json")
    suspend fun getCouponDetails(@Path("couponId") couponId: String): Response<DiscountCodesResponse>
}