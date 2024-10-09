package com.senseicoder.quickcart.core.model.graph_product

import com.storefront.GetProductByIdQuery

data class FeaturedImage(
    val width: Int = 0,
    val height: Int = 0,
    val url: String = "",
)

fun  GetProductByIdQuery.FeaturedImage?.mapQueryFeaturedImageToFeaturedImage(): FeaturedImage {
    return FeaturedImage(this?.width ?: 0, this?.height ?: 0, (this?.url ?: "").toString())
}
