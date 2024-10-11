package com.senseicoder.quickcart.core.network.customer

import com.google.gson.annotations.SerializedName

data class CustomerCreateRequest(
    @SerializedName("customer")
    val customer: Customer
)

data class Customer(
    val id: Long? = 0,
    val first_name: String?,
    val last_name: String?,
    val email: String,
    @SerializedName("verified_email")
    val verified_email: Boolean = false,
    @SerializedName("send_email_invite")
    val send_email_invite: Boolean? = true
)

data class CustomerCreateResponse(
    val customer: CustomerDetails?
)

data class CustomerDetails(
    val id: Long,
    val email: String,
    val first_name: String?,
    val last_name: String?,
    val created_at: String,
    val updated_at: String
)