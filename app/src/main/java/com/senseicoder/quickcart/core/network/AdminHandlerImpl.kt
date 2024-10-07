package com.senseicoder.quickcart.core.network
//
//import com.admin.CreateCustomerMutation
////import com.admin.GetCustomerQuery
////import com.admin.GetOrderDetailsQuery
////import com.admin.ProductsQuery
////import com.admin.UpdateCustomerMutation
////import com.admin.type.ContextualPricingContext
//import com.admin.type.CountryCode
////import com.admin.type.CustomerInput
//import com.apollographql.apollo.ApolloClient
//import com.apollographql.apollo.api.Optional
//import com.apollographql.apollo.network.okHttpClient
//import com.senseicoder.quickcart.BuildConfig
//import com.senseicoder.quickcart.core.global.Constants
//import com.senseicoder.quickcart.core.network.interfaces.AdminHandler
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import okhttp3.Interceptor
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//
//object AdminHandlerImpl : AdminHandler {
//
//    private val logging = HttpLoggingInterceptor().apply {
//        setLevel(HttpLoggingInterceptor.Level.BODY)
//    }
//
//    // Create an interceptor to add the header
//    private val headerInterceptor = Interceptor { chain ->
//        val original = chain.request()
//        val requestBuilder = original.newBuilder()
//            .header("X-Shopify-Access-Token", BuildConfig.shopify_admin_api_access_token)
//        val request = requestBuilder.build()
//        chain.proceed(request)
//    }
//
//    // Create OkHttpClient and add the interceptor
//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(headerInterceptor)
//        .addInterceptor(logging)
//        .build()
//
//    private val apolloClient = ApolloClient.Builder().serverUrl(Constants.API.ADMIN)
//        .okHttpClient(okHttpClient)
//        .build()
//
//    override fun getProducts(query: String): Flow<ProductsQuery.Data> = flow {
//        val response = apolloClient.query(
//            ProductsQuery(
//                query, ContextualPricingContext(
//                    Optional.Present(CountryCode.US),
//                )
//            )
//        ).execute()
//        if (!response.hasErrors() && response.data != null) {
//            emit(response.data!!)
//        } else {
//            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
//        }
//    }
//
//    override fun createCustomer(
//        email: String,
//        firstName: String,
//        lastName: String
//    ) = flow<CreateCustomerMutation.Data> {
//        val response = apolloClient.mutation(
//            CreateCustomerMutation(
//                CustomerInput(
//                    email = Optional.present(email),
//                    firstName = Optional.present(firstName),
//                    lastName = Optional.present(lastName)
//                )
//            )
//        ).execute()
//        if (!response.hasErrors() && response.data != null) {
//            emit(response.data!!)
//        } else {
//            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
//        }
//    }
//
//    override fun updateCustomer(
//        email: String,
//        firstName: String,
//        lastName: String,
//        id: String
//    ) = flow<UpdateCustomerMutation.Data> {
//        val response = apolloClient.mutation(
//            UpdateCustomerMutation(
//                CustomerInput(
//                    email = Optional.present(email),
//                    firstName = Optional.present(firstName),
//                    lastName = Optional.present(lastName),
//                    id = Optional.present(id)
//                )
//            )
//        ).execute()
//        if (!response.hasErrors() && response.data != null) {
//            emit(response.data!!)
//        } else {
//            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
//        }
//    }
//
//    override fun getOrderDetails(id: String) = flow<GetOrderDetailsQuery.Data> {
//        val response = apolloClient.query(
//            GetOrderDetailsQuery(
//                id
//            )
//        ).execute()
//        if (response.data != null) {
//            emit(response.data!!)
//        } else {
//            if (response.exception != null) {
//                throw Exception(response.exception!!.message)
//            } else {
//                throw Exception(Constants.Errors.UNKNOWN)
//            }
//        }
//    }
//
//    override fun getCustomer(id: String) = flow<GetCustomerQuery.Data> {
//        val response = apolloClient.query(GetCustomerQuery(id)).execute()
//        if (!response.hasErrors() && response.data != null) {
//            emit(response.data!!)
//        } else {
//            throw Exception(response.errors?.get(0)?.message ?: "Something went wrong")
//        }
//    }
//
//
//}