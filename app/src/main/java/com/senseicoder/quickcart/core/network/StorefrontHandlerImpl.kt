package com.senseicoder.quickcart.core.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.network.okHttpClient
import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.storefront.AddProductsToCartMutation
import com.storefront.CreateCartMutation
import com.storefront.CreateCustomerAccessTokenMutation
import com.storefront.CreateCustomerMutation
import com.storefront.GetCartDetailsQuery
import com.storefront.RemoveProductFromCartMutation
import com.storefront.type.CartLineInput
import com.storefront.type.CustomerCreateInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object StorefrontHandlerImpl : StorefrontHandler {

    private const val header = "X-Shopify-Storefront-Access-Token"

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // Create an interceptor to add the header
    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header(header, BuildConfig.shopify_store_front_api_access_token)
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    // Create OkHttpClient and add the interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(logging)
        .build()

    private val apolloClient = ApolloClient.Builder().serverUrl(Constants.API.STORE_FRONT)
        .okHttpClient(okHttpClient)
        .build()

    override suspend fun loginUser(
        email: String,
        password: String
    ): Flow<CreateCustomerAccessTokenMutation.CustomerAccessToken> = flow {
        val mutation = CreateCustomerAccessTokenMutation(email, password)

        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.customerAccessTokenCreate != null) {
            emit(response.data!!.customerAccessTokenCreate!!.customerAccessToken!!)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }


    override fun createCustomer(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) = flow<CreateCustomerMutation.Customer> {
        val response = apolloClient.mutation(
            CreateCustomerMutation(
                CustomerCreateInput(
                    email = email,
                    password = password,
                    firstName = if (firstName.isNotBlank()) Optional.present(firstName) else Optional.absent(),
                    lastName = if (lastName.isNotBlank()) Optional.present(lastName) else Optional.absent(),
                )
            )
        ).execute()

        if (!response.hasErrors() && response.data != null) {
            Log.d(TAG, "createCustomer: ${response.errors.toString()}")
            emit(
                response.data?.customerCreate?.customer
                    ?: throw Exception(response.data!!.customerCreate!!.customerUserErrors.map {
                        CreateCustomerMutation.CustomerUserError(
                            code = it.code,
                            field = it.field?.toList(),
                            message = it.message
                        )
                    }.joinToString { it.message })
            )
        } else {// Something wrong happened
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun createCart(email: String, token: String) = flow {
        val mutation = CreateCartMutation(email, token)

        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.cartCreate != null) {
            emit(response.data!!.cartCreate!!.cart!!)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun addToCartById(
        cartId: String,
        productsOfCart: List<ProductOfCart>
    )=flow{
        val mutation = AddProductsToCartMutation(cartId, productsOfCart.map { CartLineInput(merchandiseId = it.variantId, quantity = Optional.present(it.quantity)) })
            val response = apolloClient.mutation(mutation).execute()
        if (response.data?.cartLinesAdd != null) {
            emit(response.data!!.cartLinesAdd!!)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun getCartProducts(cartId: String) = flow<GetCartDetailsQuery.Cart> {
        val mutation = GetCartDetailsQuery(cartId)
        val response = apolloClient.query(mutation).execute()
        if (response.data?.cart!= null) {
            emit(response.data!!.cart!!)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun removeProductFromCart(cartId: String, lineId: String) = flow {
        val mutation = RemoveProductFromCartMutation(cartId, lineId)
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.cartLinesRemove != null) {
            emit(response.data!!.cartLinesRemove!!.toString())
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    private const val TAG = "StorefrontHandlerImpl"
}