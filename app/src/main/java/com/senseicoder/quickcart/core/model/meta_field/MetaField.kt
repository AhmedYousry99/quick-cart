package com.senseicoder.quickcart.core.model.meta_field

data class Metafield(
    val namespace: String,
    val key: String,
    val value: String,
    val value_type: String = "string",
    val owner_resource: String = "cart", // Assuming it's attached to a cart
    val owner_id: String
)