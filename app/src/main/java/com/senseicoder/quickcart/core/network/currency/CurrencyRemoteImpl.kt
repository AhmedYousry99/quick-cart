package com.senseicoder.quickcart.core.network.currency

import android.util.Log
import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.AllCurrencies
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.network.ApiService
import com.senseicoder.quickcart.core.services.SharedPrefsService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import kotlin.math.E

object CurrencyRemoteImpl : CurrencyRemote {
    override suspend fun getCurrencyRate(newCurrency: String): Response<CurrencyResponse>{
        val res = ApiService.currencyApiService.getLatestRates(BuildConfig.currency_api_key,
            SharedPrefsService.getSharedPrefString(Constants.CURRENCY,Constants.CURRENCY_DEFAULT),
            newCurrency)
        Log.d("", "prepareCurrencyDataAndSetListener: CurrencyRemoteImpl${res}")

        return res
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