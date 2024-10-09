package com.senseicoder.quickcart.core.model.graph_product

data class Variant(
    val id: String,
    val quantityAvailable: String,
    val currentlyNotInStock: Boolean,
    val availableForSale: Boolean,
    val image: FeaturedImage,
    val price: Price,
    val selectedOptions: List<SelectedOption>
)

data class SelectedOption(
    val name: String,
    val value: String
)


