package com.senseicoder.quickcart.core.network.currency

import com.senseicoder.quickcart.core.model.AllCurrencies
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface CurrencyRemote {
    suspend fun getCurrencyRate(newCurrency: String): Response<CurrencyResponse>
    fun getCurrencies(): Flow<AllCurrencies>

}