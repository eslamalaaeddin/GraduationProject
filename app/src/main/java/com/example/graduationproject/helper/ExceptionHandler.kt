package com.example.graduationproject.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.graduationproject.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "ExceptionHandler"

class ExceptionHandler(private val context: Context) {

    fun handleException(ex: Throwable, message: String? = null) {
        when (ex) {
            is HttpException -> {
                showErrorMessage(message)
            }
            is IOException -> {
                if (getConnectionType(context) == 0) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.check_your_connectivity),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    showErrorMessage(ex.localizedMessage)
                }
            }
            else -> {
                //showErrorMessage(ex.localizedMessage)
                Log.e(TAG, "NOT (IO||HTTP) --> ${ex.localizedMessage.orEmpty()}")
            }
        }
    }

    private fun showErrorMessage(message: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }


    private fun getConnectionType(context: Context): Int {
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
        } else {
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