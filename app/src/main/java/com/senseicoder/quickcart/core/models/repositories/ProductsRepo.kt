package com.senseicoder.quickcart.core.models.repositories

import com.admin.ProductsQuery
import kotlinx.coroutines.flow.Flow

interface ProductsRepo {
    suspend fun getProducts(query: String): Flow<ProductsQuery.Data>

}