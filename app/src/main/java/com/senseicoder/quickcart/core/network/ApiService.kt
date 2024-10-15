package com.senseicoder.quickcart.core.network

import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.network.RetrofitHelper.FormUrlEncodedConverterFactory
import com.senseicoder.quickcart.core.network.RetrofitHelper.gson
import com.senseicoder.quickcart.core.network.coupons.CouponsInterface
import com.senseicoder.quickcart.core.network.currency.CurrencyInterface
import com.senseicoder.quickcart.core.network.order.OrderInterface
import com.senseicoder.quickcart.core.network.customer.CustomerAdminRetrofitInterface
import com.senseicoder.quickcart.core.network.product.ProductsApiInterface
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

object ApiService {

    private const val CURRENCY_BASE_URL = "https://api.currencyapi.com/"

    val brandsApiService: ProductsApiInterface =
        AppRetrofit.retrofit.create(ProductsApiInterface::class.java)

    val customerApiService: CustomerAdminRetrofitInterface =
        AppRetrofit.retrofit.create(CustomerAdminRetrofitInterface::class.java)

    val orderApiService: OrderInterface =
        AppRetrofit.retrofit.create(OrderInterface::class.java)

    val couponsService: CouponsInterface =
        AppRetrofit.retrofit.create(CouponsInterface::class.java)

    val currencyApiService: CurrencyInterface =
        AppRetrofit.retrofitCurrency.create(CurrencyInterface::class.java)



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
            .callTimeout(Duration.ofSeconds(20))
            .readTimeout(Duration.ofSeconds(20))
            .build()

        private val currencyApiOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .callTimeout(Duration.ofSeconds(20))
            .readTimeout(Duration.ofSeconds(20))
            .build()

        @Volatile
        private var client = OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .addInterceptor(headerInterceptor)
            .callTimeout(Duration.ofSeconds(20))
            .readTimeout(Duration.ofSeconds(20))
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
            .client(currencyApiOkHttpClient)
            .build()

        val stripeRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.stripe.com/")
            .addConverterFactory(FormUrlEncodedConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(stripeOkHttpClient)
            .build()
    }
}