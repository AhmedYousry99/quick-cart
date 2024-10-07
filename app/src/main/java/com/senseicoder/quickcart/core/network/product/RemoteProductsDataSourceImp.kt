package com.senseicoder.quickcart.core.network.product

import com.senseicoder.quickcart.core.entity.brand.BrandResponse
import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.entity.product.ProductResponse
import com.senseicoder.quickcart.core.network.ApiService
import com.senseicoder.quickcart.core.network.interfaces.RemoteProductsDataSource

class RemoteProductsDataSourceImp(private val productsAPIInterface: ProductsApiInterface = ApiService.brandsApiService) :
    RemoteProductsDataSource {
    override suspend fun getAllBrand(): BrandResponse {
        return productsAPIInterface.getAllBrand()
    }

    override suspend fun getAllProductInBrand(brand: String): ProductResponse {
        return productsAPIInterface.getAllProducts()
    }


    override suspend fun getProductById(id: Long): ProductDetails {
        return productsAPIInterface.getProductDetails(id= id)
    }

    override suspend fun getAllProduct(): ProductResponse {
        return productsAPIInterface.getAllProducts()

    }


}