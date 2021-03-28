package com.example.graduationproject.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.widget.Toast


class NetworkStateReceiver : BroadcastReceiver() {
    var handler: Handler? = null
    override fun onReceive(context: Context, intent: Intent?) {
        if (!isOnline(context)) {
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

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }


}