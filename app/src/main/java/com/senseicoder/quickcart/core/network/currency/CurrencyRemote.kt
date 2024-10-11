package com.senseicoder.quickcart.core.network.currency

import com.senseicoder.quickcart.core.model.AllCurrencies
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.Flow

interface CurrencyRemote {
    fun getCurrencyRate(newCurrency: String): Flow<CurrencyResponse>
    fun getCurrencies(): Flow<AllCurrencies>

}