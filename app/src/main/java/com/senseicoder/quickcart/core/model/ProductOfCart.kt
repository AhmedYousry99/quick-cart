package com.senseicoder.quickcart.core.model

import com.senseicoder.quickcart.core.global.withoutGIDPrefix
import com.storefront.AddProductToCartMutation
import com.storefront.CartLinesUpdateMutation
import com.storefront.GetCartDetailsQuery


class ProductOfCart(
    val id: String,
    var quantity: Int,
    val productId: String? = null,
    val productTitle: String? = null,
    val productImageUrl: String? = null,
    val variantId: String,
    val variantTitle: String? = null,
    val variantPrice: String? = null,
    val linesId: String? = null,
    val  size:String? = null,
    val stoke : String? = null
) {
    companion object {
        fun fromEdges(edges: List<CartLinesUpdateMutation.Edge>?): List<ProductOfCart> {
            val cartProducts = mutableListOf<ProductOfCart>()
            edges?.forEach { edge ->
                val node = edge.node
                val merchandise = node.merchandise.onProductVariant
                if (merchandise != null) {
                    val productId = merchandise.id
                    val productTitle = merchandise.title
                    val productImageUrl = merchandise.image?.url
                    val variantId = merchandise.id
                    val variantTitle = merchandise.title ?: ""
                    val variantPrice = merchandise.price

                    cartProducts.add(
                        ProductOfCart(
                            id = node.id,
                            quantity = node.quantity,
                            productId = productId,
                            productTitle = productTitle,
                            productImageUrl = productImageUrl.toString(),
                            variantId = variantId,
                            variantTitle = variantTitle,
                            variantPrice = variantPrice.toString(),
                            linesId = node.id
                        )
                    )
                }
            }
            return cartProducts
        }
    }
}

fun ProductOfCart.toLineItem(): LineItem {
    return LineItem(
        title = this.productTitle ?: "",
        variant_id = this.variantId.withoutGIDPrefix().toLong(),
        quantity = this.quantity,
        price = this.variantPrice ?: ""
    )
}

@JvmName("fromGetCartDetailsQueryEdges")
fun List<GetCartDetailsQuery.Edge>?.fromEdges(): List<ProductOfCart> {
    val cartProducts = mutableListOf<ProductOfCart>()
    this?.forEach { edge ->
        val node = edge.node
        val merchandise = node.merchandise.onProductVariant
        if (merchandise != null) {
            val product = merchandise.product
            val productId = product.id
            val productTitle = product.title ?: ""
            val productImageUrl = product.featuredImage?.url ?: ""
            val variantId = merchandise.id
            val variantTitle = merchandise.title ?: ""
            val variantPrice = merchandise.price.amount

            cartProducts.add(
                ProductOfCart(
                    id = node.id,
                    quantity = node.quantity,
                    productId = productId,
                    productTitle = productTitle,
                    productImageUrl = productImageUrl.toString(),
                    variantId = variantId,
                    variantTitle = variantTitle,
                    variantPrice = variantPrice.toString(),
                    linesId = node.id,
                    size = node.merchandise.onProductVariant.title.split("[/]").first(),
                    stoke = node.merchandise.onProductVariant.quantityAvailable.toString()
                )
            )
        }
    }
    return cartProducts
}

fun List<CartLinesUpdateMutation.Edge>?.fromEdges(): List<ProductOfCart> {
    val cartProducts = mutableListOf<ProductOfCart>()
    this?.forEach { edge ->
        val node = edge.node
        val merchandise = node.merchandise.onProductVariant
        if (merchandise != null) {
            val productId = merchandise.id
            val productTitle = merchandise.title
            val productImageUrl = merchandise.image?.url
            val variantId = merchandise.id
            val variantTitle = merchandise.title ?: ""
            val variantPrice = merchandise.price

            cartProducts.add(
                ProductOfCart(
                    id = node.id,
                    quantity = node.quantity,
                    productId = productId,
                    productTitle = productTitle,
                    productImageUrl = productImageUrl.toString(),
                    variantId = variantId,
                    variantTitle = variantTitle,
                    variantPrice = variantPrice.toString(),
                    linesId = node.id
                )
            )
        }
    }
    return cartProducts
}

fun AddProductToCartMutation.Node.mapCartLinesAddProductOfCart(): ProductOfCart {
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
        variantPrice = productVariant?.price?.amount?.toString() ?: "", // Variant price as string
        linesId = productVariant?.id ?: ""
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
        variantPrice = productVariant?.price?.amount.toString(),// Variant price as string
        linesId = productVariant?.id ?: ""

    )
}