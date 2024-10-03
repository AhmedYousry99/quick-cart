package com.senseicoder.quickcart.core.network.product

import com.senseicoder.quickcart.core.entity.brand.BrandResponse
import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.entity.product.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProductsApiInterface {@GET("smart_collections.json")
//password
suspend fun getAllBrand(@Header("X-Shopify-Access-Token") passwordToken: String ="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"): BrandResponse

    @GET("products.json")
    suspend fun getAllProducts(@Header("X-Shopify-Access-Token") passwordToken: String ="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"): ProductResponse

    @GET("products/" + "{id}" + ".json")
    suspend fun getProductDetails(
        @Header("X-Shopify-Access-Token") password: String ="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",
        @Path("id") id: Long,

        ): ProductDetails
}