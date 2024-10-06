package com.senseicoder.quickcart.core.models.repositories

import android.util.Log
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.models.CustomerDTO
import com.senseicoder.quickcart.core.network.interfaces.FirebaseHandler
import com.senseicoder.quickcart.core.network.interfaces.AdminHandler
import com.senseicoder.quickcart.core.services.SharedPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip

class CustomerRepoImpl private constructor(
    private val firebaseHandler: FirebaseHandler,
    private val adminHandler: AdminHandler,
    private val sharedPrefsService: SharedPrefs
) : CustomerRepo{


    override suspend fun loginUsingNormalEmail(email: String, password: String): Flow<CustomerDTO> {
        return firebaseHandler.loginUsingNormalEmail(email, password)
    }

    override suspend fun loginUsingGuest(): Flow<CustomerDTO> {
        return firebaseHandler.loginUsingGuest()
    }

    /* override fun getCustomerUsingEmail(email: String): Flow<ApiState<CustomerDTO>> = flow {
         emit(ApiState.Loading)
         try {
             val customerResponse = remoteDataSource.getCustomersUsingEmail(email)
             if (customerResponse.isSuccessful)
             {
                 if (customerResponse.body()?.customers != null
                     && customerResponse.body()?.customers?.size!! > 0
                     && customerResponse.body()?.customers?.get(0) != null)
                 {
                     emit(State.Success(customerResponse.body()?.customers?.get(0)!!))
                 }else
                 {
                     emit(State.Error(Constants.CUSTOMER_NOT_FOUND))
                 }
             }else
             {
                 emit(State.Error(customerResponse.message()))
             }
         }catch (e : Exception)
         {
             emit(State.Error(e.message.toString()))
         }
     }*/

    override suspend fun signupUsingEmailAndPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) = flow<CustomerDTO> {
        firebaseHandler.signupUsingNormalEmail(email, password, firstName, lastName).zip(
            adminHandler.createCustomer(
                email = email,
                firstName = firstName,
                lastName = lastName
            )
        ) { dto, data ->
            Log.d(TAG, "createCustomer: success")
            dto.copy(id = data.customerCreate!!.customer!!.id)
        }.collect {
            emit(it)
        }
    }

    companion object {
        private const val TAG = "CustomerRepoImpl"

        @Volatile
        private var instance: CustomerRepoImpl? = null
        fun getInstance(
            firebaseHandler: FirebaseHandler,
            adminHandler: AdminHandler,
            sharedPrefs: SharedPrefs
        ): CustomerRepoImpl {
            return instance ?: synchronized(this) {
                val instance =
                    CustomerRepoImpl(
                        firebaseHandler,
                        adminHandler,
                        sharedPrefs
                    )
                this.instance = instance
                instance
            }
        }
    }

    override fun setUserId(value: String) {
        sharedPrefsService.setSharedPrefString(Constants.USER_ID, value)
    }

    override fun getUserId(): String {
        return sharedPrefsService.getSharedPrefString(Constants.USER_ID, Constants.USER_ID_DEFAULT)
    }

}