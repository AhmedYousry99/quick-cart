package com.senseicoder.quickcart.core.model.meta_field


import com.google.gson.annotations.SerializedName

/**
{
  "metafield": {
    "id": 1069228949,
    "namespace": "discounts",
    "key": "special",
    "value": "yes",
    "description": null,
    "owner_id": 207119551,
    "created_at": "2024-09-26T15:28:31-04:00",
    "updated_at": "2024-09-26T15:28:31-04:00",
    "owner_resource": "customer",
    "type": "single_line_text_field",
    "admin_graphql_api_id": "gid://shopify/Metafield/1069228949"
  }
}
*/
data class MetafieldsResponse(
    @SerializedName("metafield")
    val metafield: Metafield?
) {
    data class Metafield(
        @SerializedName("admin_graphql_api_id")
        val adminGraphqlApiId: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("description")
        val description: Any?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("key")
        val key: String?,
        @SerializedName("namespace")
        val namespace: String?,
        @SerializedName("owner_id")
        val ownerId: Int?,
        @SerializedName("owner_resource")
        val ownerResource: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("value")
        val value: String?
    )
}