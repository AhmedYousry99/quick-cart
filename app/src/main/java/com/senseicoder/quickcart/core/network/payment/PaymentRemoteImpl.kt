package com.senseicoder.quickcart.core.network.payment

import com.senseicoder.quickcart.core.network.ApiService
import okhttp3.ResponseBody
import retrofit2.Response

object PaymentRemoteImpl: PaymentRemote {

    override suspend fun getStripePaymentIntent(paymentRequest: PaymentRequest): Response<PaymentIntentResponse> {
        return ApiService.paymentApiService.getStripePaymentIntent(paymentRequest)
    }
}