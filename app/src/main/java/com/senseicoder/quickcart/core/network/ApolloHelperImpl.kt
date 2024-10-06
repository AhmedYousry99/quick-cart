package com.senseicoder.quickcart.core.network

import com.admin.ProductsQuery
import com.admin.type.ContextualPricingContext
import com.admin.type.CountryCode
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.network.okHttpClient
import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.network.interfaces.GraphHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ApolloHelperImpl: GraphHelper {

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    // Create an interceptor to add the header
    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("X-Shopify-Access-Token", BuildConfig.shopify_admin_api_access_token)
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    // Create OkHttpClient and add the interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(logging)
        .build()

    private val apolloClient = ApolloClient.Builder().serverUrl(Constants.API.ADMIN)
        .okHttpClient(okHttpClient)
        .build()

    override  fun getProducts(query: String): Flow<ProductsQuery.Data> = flow{
        val response = apolloClient.query(ProductsQuery(query, ContextualPricingContext(
            Optional.Present(CountryCode.US),
        ))).execute()
        if (!response.hasErrors() && response.data != null){
            emit(response.data!!)
        }
        else{
            throw Exception(response.errors?.get(0)?.message?: "Something went wrong")
        }
    }

}