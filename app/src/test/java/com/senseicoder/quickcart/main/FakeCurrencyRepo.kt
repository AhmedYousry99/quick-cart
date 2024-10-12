package com.senseicoder.quickcart.testing

import com.senseicoder.quickcart.core.model.Currency
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.model.Meta
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCurrencyRepo : CurrencyRepo {
    var shouldReturnError = false
    var currencyResponse: CurrencyResponse? = null

    override fun getCurrencyRate(newCurrency: String): Flow<CurrencyResponse> {
        return flow {
            if (shouldReturnError) {
                emit(CurrencyResponse(emptyMap(), Meta("error"))) // Simulate an error response with an empty map and meta
            } else {
                // Provide a default or sample Meta object if currencyResponse is null
                val meta = Meta(last_updated_at = "2024-10-12T00:00:00Z")
                emit(currencyResponse ?: CurrencyResponse(emptyMap(), meta)) // Simulate a success response with an empty map and meta
            }
        }
    }
}
