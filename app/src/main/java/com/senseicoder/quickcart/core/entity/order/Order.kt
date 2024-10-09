package com.senseicoder.quickcart.core.entity.order

import com.senseicoder.quickcart.core.entity.product.Product

data class Order(
    val id: String,
    val name: String,
    val address: String?,
    val totalPriceAmount: String,
    val totalPriceCurrencyCode: String,
    val subTotalPriceAmount: String,
    val subTotalPriceCurrencyCode: String,
    val totalTaxAmount: String,
    val totalTaxCurrencyCode: String,
    val processedAt: String,
    val phone :String?,
    val products: List<Product>
)
