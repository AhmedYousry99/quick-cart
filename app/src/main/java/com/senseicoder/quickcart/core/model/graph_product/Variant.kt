package com.senseicoder.quickcart.core.model.graph_product

data class Variant(
    val id: String = "0",
    val quantityAvailable: String = "0",
    val currentlyNotInStock: Boolean = false,
    val availableForSale: Boolean = false,
    val image: FeaturedImage,
    val price: Price,
    val selectedOptions: List<SelectedOption>
)

data class SelectedOption(
    val name: String,
    val value: String
)


