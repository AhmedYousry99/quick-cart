package com.senseicoder.quickcart.core.network.customer

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface CustomerAdminRetrofitInterface {

    @Headers("Content-Type: application/json")
    @POST("customers.json")
    suspend fun createCustomer(
        @Body request: CustomerCreateRequest
    ): Response<CustomerCreateResponse>

    @GET("customers/{customer_id}.json")
    suspend fun getCustomerById(
        @Path("customer_id") customerId: String
    ): Response<CustomerResponse>

}