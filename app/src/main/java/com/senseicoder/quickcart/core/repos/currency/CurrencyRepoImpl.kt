package com.senseicoder.quickcart.core.repos.currency

import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.network.currency.CurrencyRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry

class CurrencyRepoImpl(private val currencyRemote: CurrencyRemote): CurrencyRepo {
    override fun getCurrencyRate(newCurrency: String): Flow<CurrencyResponse> {
        return flow {
            currencyRemote.getCurrencyRate(newCurrency).collect {
                val res = it
                emit(res)
            }
        }
    }
}