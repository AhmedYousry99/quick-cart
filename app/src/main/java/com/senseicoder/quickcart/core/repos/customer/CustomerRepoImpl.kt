package com.senseicoder.quickcart.core.repos.customer

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
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.zip
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CustomerRepoImpl private constructor(
    private val firebaseHandler: FirebaseHandler,
    private val storefrontHandler: StorefrontHandler,
    private val sharedPrefsService: SharedPrefs,
    private val dbRemoteDataSource: RemoteDataSource,
) : CustomerRepo {

    override fun loginUsingNormalEmail(email: String, password: String): Flow<CustomerDTO> {
        return dbRemoteDataSource.getUserByIdOrAddUser(CustomerDTO(email = email)).zip(
            firebaseHandler.loginUsingNormalEmail(
                email, password
            )
        ) { dto, _ ->
            dto
        }.flatMapLatest { firebaseHandler.handleEmailVerification(it) }.flatMapConcat { dto ->
            if (dto.cartId == Constants.CART_ID_DEFAULT)
                storefrontHandler.createCart(dto.email).map {
                    dto.copy(cartId = it.id)
                }
            else
                flowOf(dto)
        }.zip(
            storefrontHandler.loginUser(
                email = email,
                password = password
            )
        ) { dto, data ->
            dto.copy(token = data.accessToken, expireAt = data.expiresAt)
        }.timeout(15.seconds)
    }

    override fun signOut() {
        firebaseHandler.signOut()
        Constants.apply {
            sharedPrefsService.setSharedPrefString(USER_ID, USER_ID_DEFAULT)
            sharedPrefsService.setSharedPrefString(USER_TOKEN, USER_TOKEN_DEFAULT)
            sharedPrefsService.setSharedPrefString(USER_EMAIL, USER_EMAIL_DEFAULT)
            sharedPrefsService.setSharedPrefString(CART_ID, CART_ID_DEFAULT)
            sharedPrefsService.setSharedPrefString(FIREBASE_USER_ID, FIREBASE_USER_ID_DEFAULT)
            sharedPrefsService.setSharedPrefString(USER_DISPLAY_NAME, USER_DISPLAY_NAME_DEFAULT)
            sharedPrefsService.setSharedPrefString(CURRENCY, CURRENCY_DEFAULT)
            sharedPrefsService.setSharedPrefFloat(
                PERCENTAGE_OF_CURRENCY_CHANGE,
                PERCENTAGE_OF_CURRENCY_CHANGE_DEFAULT
            )
        }
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

    /*@OptIn(ExperimentalCoroutinesApi::class)
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
    }*/

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun signupUsingEmailAndPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<CustomerDTO> {
        return firebaseHandler.signupUsingNormalEmail(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName
        )
            .zip(
                storefrontHandler.createCustomer(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password
                )
            ) { dto, data ->
                dto.copy(id = data.id)
            }.zip(storefrontHandler.createCart(email)) { dto, data ->
                dto.copy(cartId = data.id)
            }.flatMapLatest {
                dbRemoteDataSource.addUser(it)
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
            dbRemoteDataSource: RemoteDataSource = FirebaseFirestoreDataSource,
        ): CustomerRepoImpl {
            return instance ?: synchronized(this) {
                val instance =
                    CustomerRepoImpl(
                        firebaseHandler,
                        storefrontHandler,
                        sharedPrefs,
                        dbRemoteDataSource,
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
        return sharedPrefsService.getSharedPrefString(
            Constants.USER_TOKEN,
            Constants.USER_TOKEN_DEFAULT
        )
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

    override fun setTokenExpirationData(tokenExpirationDate: String) {
        return sharedPrefsService.setSharedPrefString(
            Constants.TOKEN_EXPIRATION_DATE,
            tokenExpirationDate
        )
    }

    override fun addFavorite(email: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        return dbRemoteDataSource.addFavorite(email, favorite)
    }

    override fun removeFavorite(email: String, favorite: FavoriteDTO): Flow<FavoriteDTO> {
        return dbRemoteDataSource.removeFavorite(email, favorite)
    }

    override fun setDisplayName(displayName: String) {
        return sharedPrefsService.setSharedPrefString(Constants.USER_DISPLAY_NAME, displayName)
    }

}