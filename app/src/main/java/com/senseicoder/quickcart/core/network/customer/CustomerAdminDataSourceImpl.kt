package com.senseicoder.quickcart.core.network.customer

import android.util.Log
import com.senseicoder.quickcart.core.global.addGIDPrefix
import com.senseicoder.quickcart.core.global.withoutGIDPrefix
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.network.interfaces.CustomerAdminDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CustomerAdminDataSourceImpl(private val api: CustomerAdminRetrofitInterface) :
    CustomerAdminDataSource {


    companion object {
        private const val TAG = "CustomerAdminDataSourceImpl"
    }

    override suspend fun createCustomer(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) = flow<CustomerDTO> {
        val response = api.createCustomer(
            CustomerCreateRequest(
                Customer(
                    first_name = firstName,
                    last_name = lastName,
                    email = email,
                )
            )
        )
        Log.d(TAG, "createCustomer: ")
        val customer = response.body()?.customer
        if(response.isSuccessful && customer != null){
            emit(CustomerDTO(email = email, displayName = "$firstName $lastName", password = password, isVerified = customer.verified_email, isGuest = false, id = customer.id.addGIDPrefix()
                ?: throw Exception("couldn't create customer, try again later")))
        }else{
            throw Exception("couldn't create customer, try again later")
        }
    }

    override fun getCustomerById(customer: CustomerDTO): Flow<CustomerDTO> = flow{
        val response = api.getCustomerById(
            customer.id.also { Log.d(TAG, "getCustomerById: $this") }.withoutGIDPrefix()
        )
        if(response.isSuccessful){
            emit(customer.copy(isVerified = response.body()?.customer?.verified_email ?: throw Exception("couldn't verify email, try again later")))
        }else{
            Log.e(TAG, "getCustomerById: ${response.errorBody()}")
            throw Exception("couldn't verify email, try again later")
        }
    }
}