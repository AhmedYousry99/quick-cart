package com.senseicoder.quickcart.core.model

data class CurrencyResponse(
    val data: Map<String, Currency>,
    val meta: Meta
)

data class Currency(
    val code: String,
    val value: Double
)

data class Meta(
    val last_updated_at: String
)

data class CurrencySymbol(val symbol:String)
data class AllCurrencies(val data:Map<String,CurrencySymbol>)