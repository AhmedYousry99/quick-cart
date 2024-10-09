package com.senseicoder.quickcart.core.network

import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.network.RetrofitHelper.FormUrlEncodedConverterFactory
import com.senseicoder.quickcart.core.network.RetrofitHelper.gson
import com.senseicoder.quickcart.core.network.product.ProductsApiInterface
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    //TODO: change url and api key
    private const val CURRENCY_BASE_URL = "https://api.getgeoapi.com/v2/currency/"
    const val CURRENCY_API_KEY = "26c960f1a769e1deed38f42d0299e5c9d496a76a"

    val brandsApiService: ProductsApiInterface =
        AppRetrofit.retrofit.create(ProductsApiInterface::class.java)

    object AppRetrofit {

        private val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        private val stripeInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
//            .header("Authorization", "Bearer $SECRET_KEY")
                .header("Content-Type", "application/x-www-form-urlencoded")
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        // Create an interceptor to add the header
        private val headerInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("X-Shopify-Access-Token", BuildConfig.shopify_admin_api_access_token)
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        private val stripeOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(stripeInterceptor)
            .addInterceptor(logging)
            .build()

        @Volatile
        private var client = OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            ).addInterceptor(headerInterceptor)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            //"https://android-alex-team5.myshopify.com/admin/api/2023-07/"
            .baseUrl(Constants.API.ADMIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val retrofitCurrency: Retrofit = Retrofit.Builder()
            .baseUrl(CURRENCY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val stripeRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.stripe.com/")
            .addConverterFactory(FormUrlEncodedConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(stripeOkHttpClient)
            .build()
    }
}