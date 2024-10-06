package com.senseicoder.quickcart.core.models.repositories

import com.admin.ProductsQuery
import com.senseicoder.quickcart.core.network.interfaces.AdminHandler
import kotlinx.coroutines.flow.Flow

class ProductsRepoImpl private constructor(private val adminHandler: AdminHandler): ProductsRepo {


    override suspend fun getProducts(query: String): Flow<ProductsQuery.Data> {
        return adminHandler.getProducts(query)
    }


    companion object {
        const val TAG: String = "AdminRepoImpl"
        @Volatile
        private var instance: ProductsRepoImpl? = null
        fun getInstance(adminHandler: AdminHandler): ProductsRepoImpl {
            return instance ?: synchronized(this){
                val instance =
                    ProductsRepoImpl(adminHandler)
                this.instance = instance
                instance
            }
        }
    }
}