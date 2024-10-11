package com.senseicoder.quickcart.core.entity.order

import com.senseicoder.quickcart.core.entity.product.Product

data class Order(
    val id: String,
    val name: String,
  //  val address: String?,
    val address :Address ?,
    val totalPriceAmount: String,
    //val totalPriceAmount: Double,
    val totalPriceCurrencyCode: String,
   val subTotalPriceAmount: String,
   // val subTotalPriceAmount: Double,
    val subTotalPriceCurrencyCode: String,
    val totalTaxAmount: String,
    //val totalTaxAmount: Double,
    val totalTaxCurrencyCode: String,
    val processedAt: String,
    val phone :String?,
    val products: List<Product>
)

data class Address(
    val address1: String?,
    val address2: String?,
    val city: String?,
    val country: String?
)