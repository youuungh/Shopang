package com.ninezero.shopang.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

class NetworkStatusViewModel(
    context: Context
) : LiveData<Boolean>() {

    private lateinit var connectivityCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val activeNetworks: MutableList<Network> = mutableListOf()
    private val ioScope = CoroutineScope(IO)

    private fun updateNetworkStatus() {
        postValue(activeNetworks.isNotEmpty())
    }

    override fun onActive() {
        connectivityCallback = createConnectivityCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, connectivityCallback)
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(connectivityCallback)
    }

    private fun createConnectivityCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val isInternetCapable = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            if (isInternetCapable == true) {
                ioScope.launch {
                    val isInternetConnected = isInternetAvailable(network.socketFactory)
                    if (isInternetConnected) {
                        withContext(Main) {
                            activeNetworks.add(network)
                            updateNetworkStatus()
                        }
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            activeNetworks.remove(network)
            updateNetworkStatus()
        }
    }

    private fun isInternetAvailable(socketFactory: SocketFactory): Boolean {
        return try {
            val socket = socketFactory.createSocket() ?: throw IOException("Socket creation failed.")
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: IOException) {
            Log.e("TAG", "No internet connection. $e")
            false
        }
    }
}