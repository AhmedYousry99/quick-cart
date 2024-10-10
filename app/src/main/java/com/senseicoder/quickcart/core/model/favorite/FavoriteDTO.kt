package com.senseicoder.quickcart.core.model.favorite

import com.google.firebase.firestore.DocumentId

data class FavoriteDTO(
    @DocumentId
    val id: String = "",
    val quantityAvailable: String = "",
    val image: List<String> = emptyList(),
    val title: String = "",
    val description: String = "",
    val priceMinimum: String = "",
    val priceMaximum: String = "",
)