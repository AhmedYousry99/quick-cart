package com.senseicoder.quickcart.core.model.graph_product

import com.senseicoder.quickcart.core.global.Constants
import com.storefront.GetProductByIdQuery

data class PriceRange(
    val maxVariantPrice: Price,
    val minVariantPrice: Price

)

private fun GetProductByIdQuery.PriceRange.mapQueryPriceRangeToPriceRange(): PriceRange {
    return PriceRange(
        this.maxVariantPrice.mapQueryPriceToPrice(),
        this.minVariantPrice.mapQueryPriceToPrice()
    )
}

private fun GetProductByIdQuery.MaxVariantPrice.mapQueryPriceToPrice(): Price {
    return Price(this.amount.toString(), this.currencyCode.toString())
}

private fun GetProductByIdQuery.MinVariantPrice.mapQueryPriceToPrice(): Price {
    return Price(this.amount.toString(), this.currencyCode.toString())
}

data class Price(val amount: String, val currencyCode: String)

data class ProductDTO(
    val id: String,
    val title: String,
    val description: String,
    val productType: String,
    val vendor: String,
    val handle: String,
    val totalInventory: Int,
    val options: List<Option>,
    val priceRange: PriceRange,
    val variants: List<Variant>,
    val images: List<FeaturedImage>,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val currency: String = Constants.CURRENCY_DEFAULT
)


fun GetProductByIdQuery.Product.mapQueryProductToProductDTO(): ProductDTO {
    return ProductDTO(
        this.id,
        this.title,
        this.description,
        this.productType,
        this.vendor,
        this.handle,
        this.totalInventory ?: 0,
        this.options.map { option ->
            Option(
                option.id,
                option.name,
                option.optionValues.map { it.mapQueryOptionValuesToOptionValues() })
        },
        this.priceRange.mapQueryPriceRangeToPriceRange(),
        this.variants.nodes.map { node -> node.mapQueryVariantNodeToVariant() },
        this.images.nodes.map { image -> FeaturedImage(url = image.url.toString()) })
}

private fun GetProductByIdQuery.Node.mapQueryVariantNodeToVariant(): Variant {
    return Variant(this.id,
        (this.quantityAvailable ?: "0").toString(),
        this.currentlyNotInStock,
        this.availableForSale,
        this.image.let { image ->
            FeaturedImage(image?.width ?: 0, image?.height ?: 0, image?.url.toString())
        },
        this.price.let { price ->
            Price(price.amount.toString(), price.currencyCode.toString())
        },
        this.selectedOptions.map { selectedOptions ->
            SelectedOption(
                selectedOptions.name,
                selectedOptions.value
            )
        })
}





