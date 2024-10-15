package com.senseicoder.quickcart.core.network.payment

import org.json.JSONObject

data class PaymentRequest(
    val map:JSONObject

)


data class StripeCustomer(
    val email: String,
    val name: String,
    val currency: String,
    val amount: String,
)
//fun StripeCustomer.toHashMap():Map<String, String>{
//    return hashMapOf(
//        "email" to email,
//        "name" to name,
//        "id" to id,
//        "currency" to phone)
//}

data class PaymentIntentResponse(
    val clientSecret: String,
    val dpmCheckerLink: String,
    val ephemeralKey: String,
    val customerId: String
)
