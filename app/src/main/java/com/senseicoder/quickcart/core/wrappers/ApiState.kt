package com.senseicoder.quickcart.core.wrappers

sealed class ApiState<out T> {
    data object Loading : ApiState<Nothing>()
    data class Success<T>(val data: T) : ApiState<T>()
    data class Failure(val error: Throwable) : ApiState<Nothing>()
}