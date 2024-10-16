package com.senseicoder.quickcart.testing

import com.senseicoder.quickcart.core.model.Currency
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.model.Meta
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


// Fake implementation of the CurrencyRepo for testing purposes
class FakeCurrencyRepo(
    private val response: CurrencyResponse? = null,
    private val shouldReturnError: Boolean = false
) : CurrencyRepo {
    override suspend fun getCurrencyRate(newCurrency: String): CurrencyResponse {
        delay(100) // Simulate some network delay
        if (shouldReturnError) {
            throw Exception("Test Exception")
        }
        return response ?: CurrencyResponse(data = emptyMap(), meta = Meta(last_updated_at = ""))
    }
}