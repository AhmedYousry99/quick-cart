package com.senseicoder.quickcart.core.network.currency

import com.senseicoder.quickcart.core.model.AllCurrencies
import com.senseicoder.quickcart.core.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyInterface {
    @GET("v3/latest")
    suspend fun getLatestRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") currencies: String
    ): Response<CurrencyResponse>

    @GET("v3/currencies")
    suspend fun getCurrencies(
        @Query("apikey") apiKey: String
    ): Response<AllCurrencies>

}