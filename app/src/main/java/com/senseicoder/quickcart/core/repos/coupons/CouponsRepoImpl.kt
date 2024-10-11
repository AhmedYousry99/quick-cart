package com.senseicoder.quickcart.core.repos.coupons

import com.senseicoder.quickcart.core.model.DiscountCodesResponse
import com.senseicoder.quickcart.core.model.PriceRulesResponse
import com.senseicoder.quickcart.core.network.coupons.CouponsRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CouponsRepoImpl(private val couponsRemote: CouponsRemote) : CouponsRepo {
    override suspend fun checkCouponDetails(couponId: String): Flow<DiscountCodesResponse> {
        return flow {
            couponsRemote.checkCouponDetails(couponId).collect {
                val res = it
                if (it.isSuccessful && it.body() != null)
                    emit(it.body()!!)
                else
                    throw Exception(it.message())
            }
        }
    }

    override suspend fun fetchCoupons(): Flow<PriceRulesResponse> {
        return flow {
            couponsRemote.fetchCoupons().collect {
                val res = it
                if (it.isSuccessful && it.body() != null)
                    emit(it.body()!!)
                else
                    throw Exception(it.message())
            }
        }
    }
}