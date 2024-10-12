package com.senseicoder.quickcart.category

import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.core.model.graph_product.FeaturedImage
import com.senseicoder.quickcart.core.model.graph_product.Price
import com.senseicoder.quickcart.core.model.graph_product.PriceRange
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.senseicoder.quickcart.core.repos.product.ProductsRepoInterface
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class FakeProductsRepo : ProductsRepoInterface {

    val sampleProducts = listOf(
        DisplayProduct(1, "Shoe 1", "SHOES", "Shoe 1", "description", "100.00"),
        DisplayProduct(2, "Shoe 2", "SHOES", "Shoe 2", "description", "150.00"),
        DisplayProduct(3, "Hat 1", "ACCESSORIES", "tag1", "description", "30.00") // Added a non-shoe product for testing


    )

    val sampleBrands = listOf(
        DisplayBrand("Brand1", "Brand One"),
        DisplayBrand("Brand2", "Brand Two"),
        DisplayBrand("Brand3", "Brand Three")
    )

    override suspend fun getAllBrand(): Flow<List<DisplayBrand>> {
        return flow {
            emit(sampleBrands)
        }
    }

    override suspend fun getAllProductInBrand(brand: String): Flow<List<DisplayProduct>> {
        // Implement logic to return products filtered by brand if needed
        return flow {
            emit(sampleProducts.filter { it.tag.contains(brand, ignoreCase = true) })
        }
    }

    override suspend fun getProductDetails(id: Long): Flow<ProductDetails> {
        // Provide a stubbed implementation for product details if needed
        TODO("Not yet implemented")
    }

    override suspend fun getAllProduct(): Flow<List<DisplayProduct>> {
        return flow {
            emit(sampleProducts)
        }
    }

    override suspend fun getCurrency(): String {
        return "USD" // Return a sample currency
    }

    override suspend fun getProductDetailsGraph(id: String): Flow<ProductDTO> {
        return flow {
            val mockPriceRange = PriceRange(
                minVariantPrice = Price(amount = "100.0", currencyCode = "USD"),
                maxVariantPrice = Price(amount = "150.0", currencyCode = "USD")
            )

            val mockFeaturedImages = listOf(
                FeaturedImage(800, 600, "https://example.com/image1.jpg")
            )

            emit(
                ProductDTO(
                    id = id,
                    title = "Mock Product",
                    description = "Mock Description",
                    productType = "SHOES",
                    vendor = "Fake Vendor",
                    handle = "mock-product",
                    totalInventory = 10,
                    options = emptyList(),
                    priceRange = mockPriceRange,
                    variants = emptyList(),
                    images = mockFeaturedImages,
                    rating = 4.5,
                    reviewCount = 20,
                    currency = "USD",
                    totalCount = 1
                )
            )
        }
    }

    override suspend fun getProductsByQuery(query: String): Flow<List<ProductDTO>> {
        return flow {
            delay(100) // Simulating network delay

            // Filter the sample products based on the query
            val filteredProducts = sampleProducts.filter { product ->
                product.title.contains(query, ignoreCase = true) ||
                        product.product_type.contains(query, ignoreCase = true)
            }

            // Map the filtered products to ProductDTO
            val productDTOs = filteredProducts.map { product ->
                ProductDTO(
                    id = product.id.toString(),
                    title = product.title,
                    description = "Mock Description", // Provide a mock description if needed
                    productType = product.product_type,
                    vendor = "Fake Vendor",
                    handle = product.title.lowercase().replace(" ", "-"),
                    totalInventory = 100,
                    options = emptyList(),
                    priceRange = PriceRange(
                        minVariantPrice = Price(amount = "100.0", currencyCode = "USD"),
                        maxVariantPrice = Price(amount = "150.0", currencyCode = "USD")
                    ),
                    variants = emptyList(),
                    images = listOf(FeaturedImage(100, 100, "https://example.com/image.jpg")),
                    rating = 4.5,
                    reviewCount = 10,
                    currency = "USD",
                    totalCount = filteredProducts.size // Update totalCount to match filtered results
                )
            }

            emit(productDTOs)
        }
    }

    }


