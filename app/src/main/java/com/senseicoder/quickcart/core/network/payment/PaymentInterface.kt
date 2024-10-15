package com.senseicoder.quickcart.core.network.payment

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentInterface {

    @POST("create-payment-intent")
    suspend fun getStripePaymentIntent(
        @Body paymentRequest: PaymentRequest
    ): Response<PaymentIntentResponse>
}