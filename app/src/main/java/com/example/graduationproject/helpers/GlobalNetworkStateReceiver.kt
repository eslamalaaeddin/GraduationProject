package com.example.graduationproject.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import com.example.graduationproject.helpers.Utils.getConnectionType


class GlobalNetworkStateReceiver : BroadcastReceiver() {
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



}