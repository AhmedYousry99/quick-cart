package com.senseicoder.quickcart.core.network.coupons

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.senseicoder.quickcart.core.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class CouponsRemoteImpl: CouponsRemote {


    override suspend fun fetchCoupons(): Flow<Response<PriceRulesResponse>> {
        return flow {
            emit(ApiService.couponsService.getCoupons())
        }
    }
    override suspend fun checkCouponDetails(couponId: String): Flow<Response<DiscountCodesResponse>>{
        return flow {
            emit(ApiService.couponsService.getCouponDetails(couponId))
        }
    }
}