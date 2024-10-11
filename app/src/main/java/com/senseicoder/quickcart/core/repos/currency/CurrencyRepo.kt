package com.senseicoder.quickcart.core.repos.currency

import com.senseicoder.quickcart.core.model.CurrencyResponse
import kotlinx.coroutines.flow.Flow

interface CurrencyRepo {
    fun getCurrencyRate(newCurrency: String): Flow<CurrencyResponse>
}