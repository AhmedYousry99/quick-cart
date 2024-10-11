package com.senseicoder.quickcart.core.network

import android.util.Log

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.network.okHttpClient
import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.entity.order.Address
import com.senseicoder.quickcart.core.entity.product.ProductDetails
import com.senseicoder.quickcart.core.entity.order.Order
import com.senseicoder.quickcart.core.entity.product.Product
import com.senseicoder.quickcart.core.entity.product.Products
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.ProductOfCart
import com.senseicoder.quickcart.core.model.fromEdges
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
import com.storefront.AddProductToCartMutation
import com.storefront.CartLinesUpdateMutation
import com.storefront.CreateAddressMutation
import com.storefront.CreateCartMutation
import com.senseicoder.quickcart.core.wrappers.ApiState
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
import com.storefront.CustomerOrdersQuery
import com.storefront.SearchQuery
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
            emit(response.data!!.customerAccessTokenCreate!!.customerAccessToken  ?: throw Exception("invalid credentials"))
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

    override suspend fun getProductsCart(cartId: String): Flow<ApolloResponse<GetCartDetailsQuery.Data>> = flow {
        val query = GetCartDetailsQuery(cartId)
        val response = apolloClient.query(query).execute()
        if (response.data != null && response.exception == null)
            emit(response)
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

    override suspend fun getProductsByQuery(query: String) = flow {
        val query = SearchQuery(query)
        val response = apolloClient.query(query).execute()
        if (response.data?.search?.nodes != null && response.exception == null) {
            emit(response.data!!.search)
        } else {
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
        }
    }

    override suspend fun getCustomerAddresses(token: String): Flow<CustomerAddressesQuery.Customer> =
        flow {
            val query = CustomerAddressesQuery(token)
            val response = apolloClient.query(query).execute()
            if (response.data?.customer != null)
                emit(response.data?.customer!!)
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
    ): Flow<CustomerDefaultAddressUpdateMutation.CustomerDefaultAddressUpdate> = flow {
        val mutation = CustomerDefaultAddressUpdateMutation(token, id)
        val response = apolloClient.mutation(mutation).execute()
        if (response.data?.customerDefaultAddressUpdate != null && response.exception == null)
            emit(
                response.data?.customerDefaultAddressUpdate!!
            )
        else
            throw response.exception ?: Exception(Constants.Errors.UNKNOWN)
    }

    //    getting orders
    override fun getCustomerOrders(token: String): Flow<ApiState<List<Order>>> = flow {
        val query = CustomerOrdersQuery(token)
        Log.i(TAG, "getOrders: ")
        try {
            emit(ApiState.Loading)
            val response: ApolloResponse<CustomerOrdersQuery.Data> =
                apolloClient.query(query).execute()

            if (response.hasErrors()) {
                Log.i(TAG, "get Orders: error" + response.errors)
                val errorMessages = response.errors?.joinToString { it.message } ?: "Unknown error"
                emit(ApiState.Failure(Throwable(errorMessages).toString()))
            } else {
                val data = response.data?.customer?.orders?.edges ?: emptyList()
                Log.i(TAG, "getOrders: data" + data)

                if (data.isEmpty()) {
                    // Handle the case where there are no orders
                    emit(ApiState.Success(emptyList())) // Emit an empty list instead of an error
                } else {
                    val orders: MutableList<Order> = mutableListOf()
                    data.forEach {
                        val products = mutableListOf<Product>()
                        it.node.apply {
                            lineItems.edges.forEach { item ->
                                products.add(
                                    Product(
                                        item.node.variant?.id ?: "",
                                        item.node.title,
                                        item.node.variant?.product?.handle ?: "",
                                        item.node.variant?.product?.description ?: "",
                                        item.node.variant?.product?.images!!.edges[0].node.url.toString(),
                                        item.node.variant.product.productType,
                                        item.node.variant.price.amount.toString(),
                                        item.node.variant.price.currencyCode.toString()
                                    )
                                )
                            }
                            // Map the billingAddress to Address object
                            val address = billingAddress?.let { addr ->
                                Address(
                                    addr.address1 ?: "",
                                    addr.address2 ?: "",
                                    addr.city ?: "",
                                    addr.country ?: ""
                                )
                            }

                            orders.add(
                                Order(
                                    id,
                                    name,
                                    address = address,
                                    currentTotalPrice.amount.toString(),
                                    currentTotalPrice.currencyCode.toString(),
                                    currentSubtotalPrice.amount.toString(),
                                    currentSubtotalPrice.currencyCode.toString(),
                                    currentTotalTax.amount.toString(),
                                    currentTotalTax.currencyCode.toString(),
                                    processedAt.toString(),
                                    phone,
                                    products
                                )
                            )
                        }
                    }
                    emit(ApiState.Success(orders))
                }
            }
        } catch (e: ApolloException) {
            Log.e(TAG, "Error fetching orders", e)
            emit(ApiState.Failure(e.toString()))
        }
    }

    private const val TAG = "StorefrontHandlerImpl"


}