package com.senseicoder.quickcart.core.network

import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.network.product.ProductsApiInterface
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    val brandsApiService: ProductsApiInterface =
        AppRetrofit.retrofit.create(ProductsApiInterface::class.java)


    object AppRetrofit {
        // Create an interceptor to add the header
        private val headerInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("X-Shopify-Access-Token", BuildConfig.shopify_admin_api_access_token)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        @Volatile
        private var client = OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            ).addInterceptor(headerInterceptor)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://android-alex-team5.myshopify.com/admin/api/2023-07/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}