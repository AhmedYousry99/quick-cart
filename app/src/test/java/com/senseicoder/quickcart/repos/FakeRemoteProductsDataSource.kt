package com.senseicoder.quickcart.core.network

import com.senseicoder.quickcart.core.entity.brand.Brand
import com.senseicoder.quickcart.core.entity.brand.BrandResponse
import com.senseicoder.quickcart.core.entity.brand.Image
import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.entity.product.ProductResponse
import com.senseicoder.quickcart.core.entity.product.Products
import com.senseicoder.quickcart.core.entity.product.Option
import com.senseicoder.quickcart.core.entity.product.Variant
import com.senseicoder.quickcart.core.entity.product.Images

import com.senseicoder.quickcart.core.network.interfaces.RemoteProductsDataSource

class FakeRemoteProductsDataSource : RemoteProductsDataSource {
    override suspend fun getAllBrand(): BrandResponse {
        // Create fake Brand instances using the properties of the Brand data class
        val fakeBrands = listOf(
            Brand(
                id = 1,
                handle = "brand-a",
                title = "Brand A",
                updated_at = "2024-10-12T12:00:00Z",
                body_html = "<p>Description of Brand A</p>",
                published_at = "2024-10-01T12:00:00Z",
                sort_order = "alpha",
                template_suffix = null,
                disjunctive = false,
                rules = emptyList(), // Assuming no rules for simplicity
                published_scope = "web",
                admin_graphql_api_id = "gid://shopify/Brand/1",
                image =null
            ),
            Brand(
                id = 2,
                handle = "brand-b",
                title = "Brand B",
                updated_at = "2024-10-12T12:00:00Z",
                body_html = "<p>Description of Brand B</p>",
                published_at = "2024-10-01T12:00:00Z",
                sort_order = "alpha",
                template_suffix = null,
                disjunctive = false,
                rules = emptyList(),
                published_scope = "web",
                admin_graphql_api_id = "gid://shopify/Brand/2",
                image = null
            )
        )
        return BrandResponse(smart_collections = fakeBrands)
    }

    override suspend fun getAllProductInBrand(brand: String): ProductResponse {
        // Create fake Product instances for testing
        val fakeProducts = when (brand) {
            "Brand A" -> listOf(
                Products(
                    id = 1L,
                    title = "Product A",
                    body_html = "<p>Description of Product A</p>",
                    vendor = "Brand A",
                    product_type = "Type A",
                    created_at = "2024-10-12T12:00:00Z",
                    handle = "product-a",
                    updated_at = "2024-10-12T12:00:00Z",
                    published_at = "2024-10-12T12:00:00Z",
                    template_suffix = "null",
                    status = "active",
                    published_scope = "web",
                    tags = "tag1, tag2",
                    admin_graphql_api_id = "gid://shopify/Product/1",
                    variants = listOf(
                        Variant(
                            id = 1L,
                            product_id = 1L,
                            title = "Variant A",
                            price = "100.0",
                            sku = "sku-a1",
                            position = 1,
                            inventory_policy = "deny",
                            compare_at_price = "null",
                            fulfillment_service = "manual",
                            inventory_management = "shopify",
                            option1 = "Size",
                            option2 = "Color",
                            option3 = "null",
                            created_at = "2024-10-12T12:00:00Z",
                            updated_at = "2024-10-12T12:00:00Z",
                            taxable = true,
                            barcode = "null",
                            grams = 200,
                            image_id = "null",
                            weight = 1,
                            weight_unit = "kg",
                            inventory_item_id = 1L,
                            inventory_quantity = 10,
                            old_inventory_quantity = 10,
                            requires_shipping = true,
                            admin_graphql_api_id = "gid://shopify/ProductVariant/1"
                        )
                    ),
                    options = emptyList(),
                    images = emptyList(),
                    image = Image(created_at = null, alt = null, width = 0, height = 0, src = "image-url")
                )
            )
            else -> emptyList()
        }
        return ProductResponse(products = fakeProducts)
    }



    override suspend fun getProductById(id: Long): ProductDetails {
        // Create a mock ProductDetails object based on your Products data class
        val mockProduct = Products(
            id = id,
            title = "Product A",
            body_html = "<p>Description of Product A</p>",
            vendor = "Vendor A",
            product_type = "Type A",
            created_at = "2024-10-12T12:00:00Z",
            handle = "product-a",
            updated_at = "2024-10-12T12:00:00Z",
            published_at = "2024-10-12T12:00:00Z",
            template_suffix = "null",
            status = "active",
            published_scope = "web",
            tags = "tag1,tag2",
            admin_graphql_api_id = "gid://shopify/Product/$id",
            variants = listOf(
                Variant(
                    id = 1L,
                    product_id = id,
                    title = "Variant A",
                    price = "100.0",
                    sku = "sku-a1",
                    position = 1,
                    inventory_policy = "deny",
                    compare_at_price = "null",
                    fulfillment_service = "manual",
                    inventory_management = "shopify",
                    option1 = "Size",
                    option2 = "Color",
                    option3 = "null",
                    created_at = "2024-10-12T12:00:00Z",
                    updated_at = "2024-10-12T12:00:00Z",
                    taxable = true,
                    barcode = "null",
                    grams = 200,
                    image_id = "null",
                    weight = 1,
                    weight_unit = "kg",
                    inventory_item_id = id,
                    inventory_quantity = 10,
                    old_inventory_quantity = 10,
                    requires_shipping = true,
                    admin_graphql_api_id = "gid://shopify/ProductVariant/1"
                )
            ),
            options = listOf(
                Option(
                    id = 1L,
                    product_id = id,
                    name = "Size",
                    position = 1,
                    values = listOf("Small", "Medium", "Large")
                )
            ),
            images = listOf(
                Images(
                    id = 1L,
                    product_id = id,
                    position = 1,
                    created_at = "2024-10-12T12:00:00Z",
                    updated_at = "2024-10-12T12:00:00Z",
                    alt = "null",
                    width = 1000,
                    height = 1000,
                    src = "image-url",
                    variant_ids = emptyList(),
                    admin_graphql_api_id = "gid://shopify/Image/$id"
                )
            ),
            image = Image(
                created_at = "2024-10-12T12:00:00Z",
                alt = null,
                width = 1000,
                height = 1000,
                src = "https://example.com/image-url.jpg"
            )

        )

        return ProductDetails(products = mockProduct)
    }

    override suspend fun getAllProduct(): ProductResponse {
        return ProductResponse(products = emptyList())
    }
}
