package com.senseicoder.quickcart.core.network.interfaces

import com.senseicoder.quickcart.core.entity.brand.BrandResponse
import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.entity.product.ProductResponse

interface RemoteProductsDataSource {

    suspend fun getAllBrand(): BrandResponse

    suspend fun getAllProductInBrand(brand: String): ProductResponse

    suspend fun getProductById(id: Long): ProductDetails

    suspend fun getAllProduct(): ProductResponse


}