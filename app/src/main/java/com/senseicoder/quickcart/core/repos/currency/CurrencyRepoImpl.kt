package com.senseicoder.quickcart.core.repos.currency

import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.network.currency.CurrencyRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry

class CurrencyRepoImpl(private val currencyRemote: CurrencyRemote): CurrencyRepo {
    override suspend fun getCurrencyRate(newCurrency: String): CurrencyResponse{
        val res = currencyRemote.getCurrencyRate(newCurrency)
        if(res.isSuccessful)
            return res.body() as CurrencyResponse
        else
            throw Exception(res.message())
    }
}