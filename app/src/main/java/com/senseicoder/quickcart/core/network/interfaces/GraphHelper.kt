package com.senseicoder.quickcart.core.network.interfaces

import com.admin.ProductsQuery
import kotlinx.coroutines.flow.Flow

interface GraphHelper {
    fun getProducts(query: String): Flow<ProductsQuery.Data>
}