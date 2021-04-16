package com.example.graduationproject.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.widget.Toast


class NetworkStateReceiver : BroadcastReceiver() {
    var handler: Handler? = null
    override fun onReceive(context: Context, intent: Intent?) {
        if (getConnectionType(context) == 0) {
            handler = Handler()
            handler?.post(object : Runnable {
                override fun run() {
                    handler?.postDelayed(this, 60000)
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            if (handler != null) {
                handler?.removeCallbacksAndMessages(null)
            }
        }
    }

    /*
        0: No Internet available (maybe on airplane mode, or in the process of joining an wi-fi).

        1: Cellular (mobile data, 3G/4G/LTE whatever).

        2: Wi-fi.

        3: VPN
     */
    fun getConnectionType(context: Context): Int {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            result = 2
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            result = 1
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                            result = 3
                        }
                    }
                }
            }
        }
        else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    when (type) {
                        ConnectivityManager.TYPE_WIFI -> {
                            result = 2
                        }
                        ConnectivityManager.TYPE_MOBILE -> {
                            result = 1
                        }
                        ConnectivityManager.TYPE_VPN -> {
                            result = 3
                        }
                    }
                }
            }
        }
        return result
    }


}