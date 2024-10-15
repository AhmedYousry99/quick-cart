package com.senseicoder.quickcart.core.network.payment

import okhttp3.ResponseBody
import retrofit2.Response

interface PaymentRemote {
    suspend fun getStripePaymentIntent(paymentRequest: PaymentRequest): Response<PaymentIntentResponse>

}