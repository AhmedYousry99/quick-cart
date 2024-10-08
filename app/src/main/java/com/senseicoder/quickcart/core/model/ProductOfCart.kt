package com.senseicoder.quickcart.core.model

import com.storefront.AddProductsToCartMutation
import com.storefront.GetCartDetailsQuery

class ProductOfCart(
    val id: String,
    var quantity: Int,
    val productId: String,
    val productTitle: String,
    val productImageUrl: String,
    val variantId: String,
    val variantTitle: String,
    val variantPrice: String
)

fun AddProductsToCartMutation.Node.mapCartLinesAddProductOfCart(): ProductOfCart {
    val productVariant = this.merchandise.onProductVariant
    val product = productVariant?.product

    return ProductOfCart(
        id = this.id,
        quantity = this.quantity, // Quantity from the cart line, not the variant's available quantity
        productId = product?.id ?: "", // Product ID from the product object
        productTitle = product?.title ?: "", // Product title
        productImageUrl = product?.images?.nodes?.firstOrNull()?.url.toString(), // First product image URL if available
        variantId = productVariant?.id ?: "", // Variant ID
        variantTitle = productVariant?.title ?: "", // Variant title
        variantPrice = productVariant?.price?.amount?.toString() ?: "" // Variant price as string
    )
}

fun GetCartDetailsQuery.Node.mapCartLineToProductOfCart(): ProductOfCart {
    val productVariant = (this.merchandise as? GetCartDetailsQuery.Merchandise)?.onProductVariant
    val product = productVariant?.product

    return ProductOfCart(
        id = this.id, // Cart line ID
        quantity = this.quantity, // Quantity from the cart line
        productId = product?.id ?: "", // Product ID
        productTitle = product?.title ?: "", // Product title
        productImageUrl = product?.featuredImage?.url.toString(), // First product image URL if available
        variantId = productVariant?.id ?: "", // Variant ID
        variantTitle = productVariant?.title ?: "", // Variant title
        variantPrice = productVariant?.price?.amount.toString() // Variant price as string
    )
}