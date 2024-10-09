package com.senseicoder.quickcart.core.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.senseicoder.quickcart.BuildConfig
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.lang.reflect.Type
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val BASE_URL = "https://android-alex-team5.myshopify.com/"
    //TODO: change url and api key
    private const val CURRENCY_BASE_URL = "https://api.getgeoapi.com/v2/currency/"
    const val CURRENCY_API_KEY = "26c960f1a769e1deed38f42d0299e5c9d496a76a"

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // Create an interceptor to add the header
    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("X-Shopify-Access-Token", BuildConfig.shopify_admin_api_access_token)
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val stripeInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            //TODO: add your header if stripe is used
//            .header("Authorization", "Bearer $SECRET_KEY")
            .header("Content-Type", "application/x-www-form-urlencoded")
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    // Create OkHttpClient and add the interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(logging)
        .build()

    private val stripeOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(stripeInterceptor)
        .addInterceptor(logging)
        .build()

    var gson: Gson = GsonBuilder()
        .setStrictness(Strictness. LENIENT)
        .create()

    // Custom converter factory for x-www-form-urlencoded
    class FormUrlEncodedConverterFactory : Converter.Factory() {
        override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<*, RequestBody>? {
            return Converter<Any, RequestBody> { value ->
                val map = value as Map<*, *>
                val builder = FormBody.Builder()
                for ((key, v) in map) {
                    builder.add(key.toString(), v.toString())
                }
                builder.build()
            }
        }
    }

    // Create the Retrofit instance with the OkHttpClient
    val retrofitInstance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
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