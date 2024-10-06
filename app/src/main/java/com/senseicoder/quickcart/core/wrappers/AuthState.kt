package com.senseicoder.quickcart.core.wrappers

import com.senseicoder.quickcart.core.models.CustomerDTO

sealed class AuthState {
    data object Loading : AuthState()
    data class Success(val user: CustomerDTO?) : AuthState()
    data class Error(val exception: Exception?) : AuthState()
}