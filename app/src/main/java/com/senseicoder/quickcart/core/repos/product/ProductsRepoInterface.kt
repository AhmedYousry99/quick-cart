package com.senseicoder.quickcart.core.repos.product

import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.model.DisplayProduct
import kotlinx.coroutines.flow.Flow

interface ProductsRepoInterface {
    suspend fun getAllBrand() : Flow<List<DisplayBrand>>
    suspend fun getAllProductInBrand(brand: String) : Flow<List<DisplayProduct>>
    suspend fun getProductDetails(id: Long): Flow<ProductDetails>
    suspend fun getAllProduct() : Flow<List<DisplayProduct>>

}