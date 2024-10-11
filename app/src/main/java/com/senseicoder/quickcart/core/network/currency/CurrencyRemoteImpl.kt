package com.senseicoder.quickcart.core.network.currency

import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.model.AllCurrencies
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.E

object CurrencyRemoteImpl : CurrencyRemote {
    override fun getCurrencyRate(newCurrency: String): Flow<CurrencyResponse> = flow {
        try {
            val res = ApiService.currencyApiService.getLatestRates(
                BuildConfig.currency_api_key,
                "EGP",
                newCurrency
            )
            if (res.isSuccessful) {
                emit(res.body()!!)
            } else {
                throw Exception(res.message())
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override fun getCurrencies(): Flow<AllCurrencies> {
        return flow {
            try {
                val res = ApiService.currencyApiService.getCurrencies(BuildConfig.currency_api_key)
                if (res.isSuccessful) {
                    emit(res.body()!!)
                } else {
                    throw Exception(res.message())
                }
            }catch (e:Exception)
            {
                throw Exception(e.message)
            }
        }
    }
}