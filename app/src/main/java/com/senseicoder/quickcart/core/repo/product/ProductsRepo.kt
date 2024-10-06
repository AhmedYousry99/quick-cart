package com.senseicoder.quickcart.core.repo.product

import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.model.DisplayBrand
import com.senseicoder.quickcart.core.model.DisplayProduct
import com.senseicoder.quickcart.core.remote.product.RemoteProductsDataSource
import com.senseicoder.quickcart.core.remote.product.RemoteProductsDataSourceImp
import com.senseicoder.quickcart.core.entity.product.mapRemoteProductToDisplayProduct
import com.senseicoder.quickcart.core.entity.brand.mapRemoteBrandToDisplayBrand

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ProductsRepo(private val remoteProductsDataSource: RemoteProductsDataSource = RemoteProductsDataSourceImp()) :
    ProductsRepoInterface {

    override suspend fun getAllBrand(): Flow<List<DisplayBrand>> {
        return flowOf(remoteProductsDataSource.getAllBrand().smart_collections?.map {
            it.mapRemoteBrandToDisplayBrand()
        }) as Flow<List<DisplayBrand>>

    }

    override suspend fun getAllProductInBrand(brand: String): Flow<List<DisplayProduct>> {
        return flowOf(remoteProductsDataSource.getAllProductInBrand(brand).products.filter {
            it.vendor == brand
        }.map {
            it.mapRemoteProductToDisplayProduct()
        })
    }

    override suspend fun getProductDetails(id: Long): Flow<ProductDetails> {
        return flowOf(remoteProductsDataSource.getProductById(id))
    }

    override suspend fun getAllProduct(): Flow<List<DisplayProduct>> {

        return flowOf(remoteProductsDataSource.getAllProduct().products.map { it.mapRemoteProductToDisplayProduct() })

    }

}