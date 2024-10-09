package com.senseicoder.quickcart.core.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.network.okHttpClient
import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.model.fromEdges
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.storefront.CartLinesUpdateMutation
import com.storefront.CreateAddressMutation
import com.storefront.CreateCartMutation
import com.storefront.CreateCustomerAccessTokenMutation
import com.storefront.CreateCustomerMutation
import com.storefront.CustomerAddressUpdateMutation
import com.storefront.CustomerAddressesQuery
import com.storefront.CustomerDefaultAddressUpdateMutation
import com.storefront.DeleteAddressMutation
import com.storefront.GetCartDetailsQuery
import com.storefront.GetProductByIdQuery
import com.storefront.RemoveProductFromCartMutation
import com.storefront.type.CartLineUpdateInput
import com.storefront.type.CustomerCreateInput
import com.storefront.type.MailingAddressInput
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
        if (response.data?.customerAccessTokenCreate != null && response.exception == null) {
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
    ) = flow {
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
        if (response.data?.customerCreate != null && response.exception == null) {
            emit(response.data!!.customerCreate!!.customer!!)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun updateQuantityOfProduct(
        cartId: String,
        lineId: String,
        quantity: Int
    ): Flow<CartLinesUpdateMutation.Lines?> = flow {
        val mutation = CartLinesUpdateMutation(
            cartId,
            listOf(CartLineUpdateInput(lineId, quantity = Optional.present(quantity)))
        )
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.cartLinesUpdate != null) {
            emit(response.data?.cartLinesUpdate?.cart?.lines)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }

    }

    override suspend fun removeProductFromCart(
        cartId: String,
        lineId: String
    ): Flow<RemoveProductFromCartMutation.CartLinesRemove?> = flow {
        val mutation = RemoveProductFromCartMutation(cartId, lineId)
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.cartLinesRemove != null) {
            emit(response.data!!.cartLinesRemove)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun getProductsCart(cartId: String): Flow<List<ProductOfCart>?> = flow {
        val query = GetCartDetailsQuery(cartId)
        val response = apolloClient.query(query).execute()
        if (response.data != null && response.exception == null)
            emit(response.data?.cart?.lines?.edges?.fromEdges())
        else
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
    }

    override suspend fun createCart(email: String) = flow {
        val mutation = CreateCartMutation(email)

        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.cartCreate != null && response.exception == null) {
            emit(response.data!!.cartCreate!!.cart!!)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun addToCartById(
        cartId: String,
        quantity: Int,
        variantId: String
    ) = flow {
        Log.d(
            TAG,
            "addToCartById: ${"cartId: $cartId, quantity: $quantity, variantId: $variantId"}"
        )
        val mutation = AddProductToCartMutation(
            cartId,
            quantity,
            variantId
        )
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.cartLinesAdd != null && response.exception == null) {
            emit(response.data!!.cartLinesAdd!!)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    //ADDRESS NEEDED

    override suspend fun createAddress(
        customerAddress: MailingAddressInput,
        token: String
    ): Flow<String?> = flow {
        val mutation = CreateAddressMutation(customerAddress, token)
        Log.d(TAG, "createAddress: ${customerAddress.country}")
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.customerAddressCreate != null && response.exception == null) {
            emit(response.data!!.customerAddressCreate!!.customerAddress?.firstName)
            Log.d(
                TAG,
                "createAddress: ${response.data?.customerAddressCreate?.customerUserErrors?.map { it.message }}"
            )
        } else
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
    }


    override suspend fun getProductDetailsById(id: String) = flow {
        val query = GetProductByIdQuery(id)
        val response = apolloClient.query(query).execute()
        if (response.data?.product != null && response.exception == null) {
            emit(response.data?.product)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun getCustomerAddresses(token: String): Flow<CustomerAddressesQuery.Customer?> =
        flow {
            val query = CustomerAddressesQuery(token)
            val response = apolloClient.query(query).execute()
            if (response.data?.customer != null)
                emit(response.data!!.customer)
            else
                throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }

    override suspend fun updateCustomerAddress(
        address: MailingAddressInput,
        token: String,
        id: String,
    ): Flow<String?> = flow {
        val mutation = CustomerAddressUpdateMutation(address, token, id)
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.customerAddressUpdate != null && response.exception == null) {
            emit(response.data!!.customerAddressUpdate!!.customerAddress!!.id)
        } else {
            if (response.data?.customerAddressUpdate != null)
                emit(response.data!!.customerAddressUpdate!!.customerAddress?.id)
            else
                throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun deleteAddress(id: String, token: String): Flow<String?> = flow {
        val mutation = DeleteAddressMutation(id, token)
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.customerAddressDelete != null && response.exception == null)
            emit(response.data!!.customerAddressDelete!!.deletedCustomerAddressId)
        else
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
    }

    override suspend fun updateDefaultAddress(
        token: String,
        id: String
    ): Flow<List<CustomerDefaultAddressUpdateMutation.Node>?> = flow {
        val mutation = CustomerDefaultAddressUpdateMutation(token, id )
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.customerDefaultAddressUpdate != null && response.exception == null)
            emit(
                response.data!!.customerDefaultAddressUpdate?.customer?.addresses?.nodes
                    ?: emptyList()
            )
        else
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
    }

    private const val TAG = "StorefrontHandlerImpl"
}