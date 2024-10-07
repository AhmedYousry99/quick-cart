package com.senseicoder.quickcart.core.models.repositories

import android.util.Log
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.models.CustomerDTO
import com.senseicoder.quickcart.core.network.interfaces.FirebaseHandler
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
//import com.senseicoder.quickcart.core.network.interfaces.AdminHandler
import com.senseicoder.quickcart.core.services.SharedPrefs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class CustomerRepoImpl private constructor(
    private val firebaseHandler: FirebaseHandler,
    private val storefrontHandler: StorefrontHandler,
    private val sharedPrefsService: SharedPrefs
) : CustomerRepo{

    override suspend fun loginUsingNormalEmail(email: String, password: String): Flow<CustomerDTO> {
        return firebaseHandler.loginUsingNormalEmail(email, password).zip(storefrontHandler.loginUser(
            email = email,
            password = password
        )){ dto, data ->
            dto.copy(token = data.accessToken, expireAt = data.expiresAt)
        }.timeout(15.seconds)
    }

    override suspend fun loginUsingGuest(): Flow<CustomerDTO> {
        return firebaseHandler.loginUsingGuest().timeout(15.seconds)
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
    ):Flow<CustomerDTO> {
        return firebaseHandler.signupUsingNormalEmail(email, password, firstName, lastName).zip(
            storefrontHandler.createCustomer(
                email = email,
                firstName = firstName,
                lastName = lastName,
                password = password
            )
        ) { dto, data ->
            Log.d(TAG, "createCustomer: success")
            dto.copy(id = data.id)
        }
            .map {
                firebaseHandler.updateDisplayName(it)
                it}
            .timeout(15.seconds)
    }

    companion object {
        private const val TAG = "CustomerRepoImpl"

        @Volatile
        private var instance: CustomerRepoImpl? = null
        fun getInstance(
            firebaseHandler: FirebaseHandler,
            storefrontHandler: StorefrontHandler,
            sharedPrefs: SharedPrefs
        ): CustomerRepoImpl {
            return instance ?: synchronized(this) {
                val instance =
                    CustomerRepoImpl(
                        firebaseHandler,
                        storefrontHandler,
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

    override fun setUserToken(token: String) {
        sharedPrefsService.setSharedPrefString(Constants.USER_TOKEN, token)
    }

    override fun getUserToken(): String {
        return sharedPrefsService.getSharedPrefString(Constants.USER_TOKEN, Constants.USER_TOKEN_DEFAULT)
    }

}