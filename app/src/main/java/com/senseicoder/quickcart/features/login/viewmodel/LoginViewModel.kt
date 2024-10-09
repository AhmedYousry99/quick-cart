package com.senseicoder.quickcart.features.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.customer.CustomerDTO
import com.senseicoder.quickcart.core.repos.customer.CustomerRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val customerRepo: CustomerRepo) : ViewModel() {


    private val _loginState = MutableStateFlow<ApiState<CustomerDTO>>(ApiState.Init)
    val loginState = _loginState.asStateFlow()

    fun loginUsingNormalEmail(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = ApiState.Loading
            customerRepo.loginUsingNormalEmail(email = email, password = password).catch { e ->
                _loginState.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
            }.collect {
                customerRepo.setUserId(it.id)
                customerRepo.setUserToken(it.token)
                customerRepo.setEmail(it.email)
                customerRepo.setDisplayName(it.displayName)
                customerRepo.setCartId(it.cartId)
                withContext(Dispatchers.Main){
                    _loginState.value = ApiState.Success(it)
                }
            }
        }
    }

    fun signupAsGuest() {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = ApiState.Loading
            customerRepo.loginUsingGuest().catch { e ->
                _loginState.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
            }.collect {
//                customerRepo.setUserId(Constants.USER_ID_DEFAULT)
                customerRepo.setUserToken(Constants.USER_TOKEN_DEFAULT)
                withContext(Dispatchers.Main){
                    _loginState.value = ApiState.Success(it)
                }
            }
        }
    }

    companion object{
        private const val TAG = "LoginViewModel"
    }

}