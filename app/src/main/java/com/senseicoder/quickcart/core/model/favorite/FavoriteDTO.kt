package com.senseicoder.quickcart.core.model.favorite

import com.google.firebase.firestore.DocumentId

data class FavoriteDTO(
    @DocumentId
    val id: String = "",
    val quantityAvailable: String = "",
    val currentlyNotInStock: Boolean = true,
    val availableForSale: Boolean = false,
    val image: String = "",
    val price: Double = 0.0,
)