package com.senseicoder.quickcart.core.models.repositories

//import com.admin.ProductsQuery
//import com.senseicoder.quickcart.core.network.interfaces.AdminHandler
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import kotlinx.coroutines.flow.Flow

class ProductsRepoImpl private constructor(private val storefrontHandler: StorefrontHandler): ProductsRepo {


    /*override suspend fun getProducts(query: String): Flow<ProductsQuery.Data> {
        return adminHandler.getProducts(query)
    }*/


    companion object {
        const val TAG: String = "AdminRepoImpl"
        @Volatile
        private var instance: ProductsRepoImpl? = null
        fun getInstance(storefrontHandler: StorefrontHandler): ProductsRepoImpl {
            return instance ?: synchronized(this){
                val instance =
                    ProductsRepoImpl(storefrontHandler)
                this.instance = instance
                instance
            }
        }
    }
}