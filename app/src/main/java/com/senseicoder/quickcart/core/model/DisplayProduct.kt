package com.senseicoder.quickcart.core.model

data class DisplayProduct(
    val id: Long,
    val price: String,
    val product_type: String,
    val title: String,
    val image: String,
    val tag : String
)
