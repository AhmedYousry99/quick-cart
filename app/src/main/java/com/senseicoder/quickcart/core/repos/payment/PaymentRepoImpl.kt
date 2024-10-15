package com.senseicoder.quickcart.core.repos.payment

import com.senseicoder.quickcart.core.network.payment.PaymentRemote
import com.senseicoder.quickcart.core.network.payment.PaymentRequest
import com.senseicoder.quickcart.core.network.payment.PaymentIntentResponse
import com.senseicoder.quickcart.core.network.payment.StripeCustomer
import okhttp3.ResponseBody
import retrofit2.Response

class PaymentRepoImpl(private val paymentRemote: PaymentRemote) : PaymentRepo {


    override suspend fun getStripePaymentIntent(paymentRequest: PaymentRequest): Response<PaymentIntentResponse> {
        return paymentRemote.getStripePaymentIntent(paymentRequest)
    }
}