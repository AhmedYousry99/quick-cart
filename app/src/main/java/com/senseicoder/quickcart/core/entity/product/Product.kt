package com.senseicoder.quickcart.core.entity.product

import com.senseicoder.quickcart.core.model.DisplayProduct

data class Product(
    val id: String,
    val title: String,
    val handle: String,
    val description: String,
    val imageUrl: String,
    val productType: String,
    val price: String,
    val currencyCode: String,
    var convertedPrice: Double? = price.toDouble(),
    var percentage:Float? = null
)

//fun Product.mapApiRemoteProductToDisplayProduct(): DisplayProduct {
//    return DisplayProduct(this.id,this.variants[0].price, this.product_type,this.title ?: "", this.image?.src ?: "", this.tags)
//}
