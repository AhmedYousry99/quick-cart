package com.senseicoder.quickcart.core.repos.payment

import com.senseicoder.quickcart.core.network.payment.PaymentRequest
import com.senseicoder.quickcart.core.network.payment.PaymentIntentResponse
import com.senseicoder.quickcart.core.network.payment.StripeCustomer
import okhttp3.ResponseBody
import retrofit2.Response

interface PaymentRepo {
    suspend fun getStripePaymentIntent(paymentRequest: PaymentRequest): Response<PaymentIntentResponse>


}