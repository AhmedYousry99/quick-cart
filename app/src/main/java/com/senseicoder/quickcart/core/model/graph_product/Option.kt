package com.senseicoder.quickcart.core.model.graph_product

import com.storefront.GetProductByIdQuery


data class Option(
    val id: String,
    val name: String,
    val values: List<OptionValues>,
)
data class OptionValues(
    val id: String,
    val name: String,
)

fun GetProductByIdQuery.OptionValue.mapQueryOptionValuesToOptionValues(): OptionValues {
    return OptionValues(
        id = this.id,
        name = this.name
    )
}
