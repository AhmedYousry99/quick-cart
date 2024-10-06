package com.senseicoder.quickcart.core.models.repositories

import com.admin.ProductsQuery
import com.senseicoder.quickcart.core.network.interfaces.GraphHelper
import kotlinx.coroutines.flow.Flow

class AdminRepoImpl private constructor(private val graphHelper: GraphHelper): AdminRepo {


    override suspend fun getProducts(query: String): Flow<ProductsQuery.Data> {
        return graphHelper.getProducts(query)
    }


    companion object {
        const val TAG: String = "AdminRepoImpl"
        @Volatile
        private var instance: AdminRepoImpl? = null
        fun getInstance(graphHelper: GraphHelper): AdminRepoImpl {
            return instance ?: synchronized(this){
                val instance =
                    AdminRepoImpl(graphHelper)
                this.instance = instance
                instance
            }
        }
    }
}