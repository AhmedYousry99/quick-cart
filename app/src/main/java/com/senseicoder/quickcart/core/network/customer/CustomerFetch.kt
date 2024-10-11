package com.senseicoder.quickcart.core.network.customer

import com.google.gson.annotations.SerializedName

// Customer response from Admin API
data class CustomerResponse(
    @SerializedName("customer") val customer: Customer
)
