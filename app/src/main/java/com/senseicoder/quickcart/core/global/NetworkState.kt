package com.senseicoder.quickcart.core.global

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkState(private val context: Context) {
    // Function to observe network changes as Flow
    fun observeNetworkState(): Flow<Boolean> = callbackFlow {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Create a NetworkCallback to receive network state changes
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true) // Network is available
            }

            override fun onLost(network: Network) {
                trySend(false) // Network is lost
            }
        }

        // Register the network callback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Initial check for network state
        val isConnected = connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
        trySend(isConnected)

        // Close the callbackFlow when the flow is no longer observed
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

}