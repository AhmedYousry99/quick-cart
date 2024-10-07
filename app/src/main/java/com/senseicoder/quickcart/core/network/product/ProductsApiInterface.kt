package com.senseicoder.quickcart.core.network.product

import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.entity.brand.BrandResponse
import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.entity.product.ProductResponse
import com.senseicoder.quickcart.core.model.SignupCustomResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductsApiInterface {@GET("smart_collections.json")
//password
suspend fun getAllBrand(@Header("X-Shopify-Access-Token") passwordToken: String = BuildConfig.shopify_admin_api_access_token): BrandResponse

    @GET("products.json")
    suspend fun getAllProducts(@Header("X-Shopify-Access-Token") passwordToken: String =BuildConfig.shopify_admin_api_access_token): ProductResponse



    @GET("products/" + "{id}" + ".json")
    suspend fun getProductDetails(
        @Header("X-Shopify-Access-Token") password: String =BuildConfig.shopify_admin_api_access_token,
        @Path("id") id: Long,

        ): ProductDetails
}