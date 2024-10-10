package com.senseicoder.quickcart.core.repos.customer

import android.util.Log
import com.senseicoder.quickcart.core.db.remote.FirebaseFirestoreDataSource
import com.senseicoder.quickcart.core.db.remote.RemoteDataSource
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.model.favorite.FavoriteDTO
import com.senseicoder.quickcart.core.network.FirebaseHandlerImpl
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.interfaces.FirebaseHandler
import com.senseicoder.quickcart.core.network.interfaces.StorefrontHandler
//import com.senseicoder.quickcart.core.network.interfaces.AdminHandler
import com.senseicoder.quickcart.core.services.SharedPrefs
import com.senseicoder.quickcart.core.services.SharedPrefsService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CustomerRepoImpl private constructor(
    private val firebaseHandler: FirebaseHandler,
    private val storefrontHandler: StorefrontHandler,
    private val sharedPrefsService: SharedPrefs,
    private val dbRemoteDataSource: RemoteDataSource
) : CustomerRepo {

    override suspend fun loginUsingNormalEmail(email: String, password: String): Flow<CustomerDTO> {
        return firebaseHandler.loginUsingNormalEmail(email, password).zip(storefrontHandler.loginUser(
            email = email,
            password = password
        )){ dto, data ->
            dto.copy(token = data.accessToken.apply {
                Log.d(TAG, "loginUsingNormalEmail: $this")
            }, expireAt = data.expiresAt )
        }.flatMapConcat {
            dbRemoteDataSource.getUserByEmail(it)
        }.timeout(15.seconds)
    }

    override suspend fun loginUsingGuest(): Flow<CustomerDTO> {
        return firebaseHandler.loginUsingGuest().timeout(15.seconds)
    }

    override fun signOut() {
        firebaseHandler.signOut()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signupUsingEmailAndPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<CustomerDTO> {
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
            .flatMapConcat {
                firebaseHandler.updateDisplayName(it)
                }
            .flatMapConcat {customer ->
                storefrontHandler.createCart(customer.email).map {
                    customer.copy(cartId = it.id)
                }
            }.flatMapConcat {
                dbRemoteDataSource.getUserByIdOrAddUser(it)
            }
            .timeout(20.seconds)
    }

    companion object {
        private const val TAG = "CustomerRepoImpl"

        @Volatile
        private var instance: CustomerRepoImpl? = null
        fun getInstance(
            firebaseHandler: FirebaseHandler = FirebaseHandlerImpl,
            storefrontHandler: StorefrontHandler = StorefrontHandlerImpl,
            sharedPrefs: SharedPrefs = SharedPrefsService,
            dbRemoteDataSource: RemoteDataSource = FirebaseFirestoreDataSource
        ): CustomerRepoImpl {
            return instance ?: synchronized(this) {
                val instance =
                    CustomerRepoImpl(
                        firebaseHandler,
                        storefrontHandler,
                        sharedPrefs,
                        dbRemoteDataSource
                    )
                Companion.instance = instance
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

    override fun setEmail(email: String) {
        return sharedPrefsService.setSharedPrefString(Constants.USER_EMAIL, email)
    }

    override fun setCartId(cartId: String) {
        return sharedPrefsService.setSharedPrefString(Constants.CART_ID, cartId)
    }

    override fun getCartId(): String {
        return sharedPrefsService.getSharedPrefString(Constants.CART_ID, Constants.CART_ID_DEFAULT)
    }

    override fun setFirebaseId(firebaseId: String) {
        return sharedPrefsService.setSharedPrefString(Constants.FIREBASE_USER_ID, firebaseId)
    }

    override suspend fun addFavorite(email: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        return dbRemoteDataSource.addFavorite(email, favorite)
    }

    override suspend fun removeFavorite(email: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        return dbRemoteDataSource.removeFavorite(email, favorite)
    }

    override fun setDisplayName(displayName: String) {
        return sharedPrefsService.setSharedPrefString(Constants.USER_DISPLAY_NAME, displayName)
    }

}