package com.senseicoder.quickcart.core.repos.product

import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.core.model.graph_product.ProductDTO
import com.storefront.SearchQuery
import kotlinx.coroutines.flow.Flow

interface ProductsRepoInterface {
    suspend fun getAllBrand() : Flow<List<DisplayBrand>>

    suspend fun getAllProductInBrand(brand: String) : Flow<List<DisplayProduct>>

    suspend fun getProductDetails(id: Long): Flow<ProductDetails>

    suspend fun getAllProduct() : Flow<List<DisplayProduct>>

    suspend fun getCurrency(): String

    suspend fun getProductDetailsGraph(id: String): Flow<ProductDTO>



    suspend fun getProductsByQuery(query: String): Flow<List<ProductDTO>>
}