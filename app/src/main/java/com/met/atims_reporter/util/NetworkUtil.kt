@file:Suppress("unused")

package com.met.atims_reporter.util

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {

    fun isConnected(context: Context): Boolean {
        val connectivityManager: ConnectivityManager? =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.let {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.let { return networkInfo.isConnected } ?: run { return false }
        } ?: run {
            return false
        }
    }
}