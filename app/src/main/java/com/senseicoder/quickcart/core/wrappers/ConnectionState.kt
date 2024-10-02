package com.senseicoder.quickcart.core.wrappers

sealed class ConnectionStatus {
    data object Available :ConnectionStatus()
    data object Unavailable : ConnectionStatus()
}