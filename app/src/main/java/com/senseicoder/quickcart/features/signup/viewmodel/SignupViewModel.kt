package com.senseicoder.quickcart.features.signup.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.models.CustomerDTO
import com.senseicoder.quickcart.core.models.repositories.CustomerRepo
import com.senseicoder.quickcart.core.wrappers.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SignupViewModel(private val customerRepo: CustomerRepo) : ViewModel() {


    private val _signUpState = MutableStateFlow<ApiState<CustomerDTO>>(ApiState.Init)
    val signUpState = _signUpState.asStateFlow()


    fun signUpUsingEmailAndPassword(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ) {
        viewModelScope.launch {
            _signUpState.value = ApiState.Loading
            customerRepo.signupUsingEmailAndPassword(
                email = email,
                firstName = firstName,
                lastName = lastName,
                password = password
            ).catch { e ->
                Log.e(TAG, "signUpUsingEmailAndPassword: ", e)
                _signUpState.value = ApiState.Failure(e.message ?: Constants.Errors.UNKNOWN)
            }.collect {
                customerRepo.setUserId(it.id)
                _signUpState.value = ApiState.Success(it)
            }
        }

    }

companion object{
    private const val TAG = "SignupViewModel"
}

}