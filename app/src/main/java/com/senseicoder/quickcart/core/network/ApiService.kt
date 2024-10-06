package com.senseicoder.quickcart.core.network

import com.senseicoder.quickcart.core.network.product.ProductsApiInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    val brandsApiService: ProductsApiInterface =
        AppRetrofit.retrofit.create(ProductsApiInterface::class.java)


    object AppRetrofit {
        @Volatile
        private var client = OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://android-alex-team5.myshopify.com/admin/api/2023-07/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}