package com.senseicoder.quickcart.core.repos.coupons

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.senseicoder.quickcart.core.network.coupons.CouponsRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CouponsRepoImpl(private val couponsRemote: CouponsRemote) : CouponsRepo {
    override suspend fun checkCouponDetails(couponId: String):DiscountCodesResponse{
        val res =  couponsRemote.checkCouponDetails(couponId)
        if (res.isSuccessful && res.body() != null)
            return res.body()!!
        else
            throw Exception(res.message())
    }

    override suspend fun fetchCoupons(): PriceRulesResponse {
        val res = couponsRemote.fetchCoupons()
        if (res.isSuccessful && res.body() != null)
            return res.body()!!
        else
            throw Exception(res.message())
    }
}