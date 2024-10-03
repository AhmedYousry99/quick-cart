package com.senseicoder.quickcart.core.wrappers

sealed class RemoteStatus<out T> {
    data object Loading : RemoteStatus<Nothing>()
    data class Success<T>(val data: T) : RemoteStatus<T>()
    data class Failure(val error: Throwable) : RemoteStatus<Nothing>()
}