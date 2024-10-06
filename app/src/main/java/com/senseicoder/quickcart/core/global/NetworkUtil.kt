package com.senseicoder.quickcart.core.global

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.senseicoder.quickcart.core.wrappers.ConnectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkUtils{

    private var _connectionState: MutableStateFlow<ConnectionStatus> = MutableStateFlow(ConnectionStatus.Unavailable)
    private var connectionState: StateFlow<ConnectionStatus> = _connectionState

    fun observeNetworkConnectivity(context: Context) : StateFlow<ConnectionStatus>{
        if(_connectionState == null){
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val request =
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()


            connectivityManager.registerNetworkCallback(request, object : NetworkCallback() {

                override fun onAvailable(network: Network) {
                    _connectionState.let{
                        it.value = ConnectionStatus.Available
                    }
                }
                override fun onLost(network: Network) {
                    _connectionState.let{
                        it.value = ConnectionStatus.Unavailable
                    }
                }
            })
        }
        return connectionState
    }

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}

