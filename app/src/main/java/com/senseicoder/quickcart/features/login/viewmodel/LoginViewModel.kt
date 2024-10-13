package com.senseicoder.quickcart.features.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.repos.customer.CustomerRepo
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LoginViewModel(private val customerRepo: CustomerRepo) : ViewModel() {


    private val _loginState = MutableSharedFlow<ApiState<CustomerDTO>>()
    val loginState = _loginState.asSharedFlow()

    fun loginUsingNormalEmail(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.emit(ApiState.Loading)
            customerRepo.loginUsingNormalEmail(email = email, password = password).catch { e ->
                _loginState.emit(value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN))
            }.collect {
                customerRepo.setUserId(it.id)
                customerRepo.setUserToken(it.token)
                customerRepo.setEmail(it.email)
                customerRepo.setDisplayName(it.displayName)
                customerRepo.setCartId(it.cartId)
                customerRepo.setFirebaseId(it.firebaseId)
                customerRepo.setTokenExpirationData(it.expireAt.toString())
                SharedPrefsService.logAllSharedPref(TAG, "loginUsingNormalEmail ${it.expireAt}")
                _loginState.emit(value = ApiState.Success(it))
            }
        }
    }

    companion object{
        private const val TAG = "LoginViewModel"
    }

}