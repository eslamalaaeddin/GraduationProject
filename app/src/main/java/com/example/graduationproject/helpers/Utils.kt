package com.example.graduationproject.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.models.authentication.RefreshToken
import com.example.graduationproject.ui.activities.MainActivity
import com.example.graduationproject.ui.activities.RegisterActivity
import com.example.graduationproject.ui.activities.SplashActivity
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {

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

//    private fun tokenizeUser(accessTokenExTime: String?, refreshTokenExTime: String?) {
//        val accessTokenExTimestamp = convertServerTimeToTimestamp(accessTokenExTime.orEmpty())
//        val refreshTokenExTimestamp = convertServerTimeToTimestamp(refreshTokenExTime.orEmpty())
//
////        Toast.makeText(this, "${isAccessTokenExpired(accessTokenExTimestamp)}", Toast.LENGTH_SHORT).show()
//        if (!SplashActivity.getAccessToken(this).isNullOrEmpty()) {
//            if (isAccessTokenExpired(accessTokenExTimestamp)) {
//                if (isRefreshTokenExpired(refreshTokenExTimestamp)) {
//                    Toast.makeText(this, "Navigate to Login", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this, RegisterActivity::class.java))
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    finish()
//                } else {
//                    val oldAccessToken = SplashActivity.getAccessToken(this).toString()
//                    val refreshToken = SplashActivity.getRefreshToken(this).toString()
//                    Log.i(com.example.graduationproject.ui.activities.TAG, "TOKEN old access token: $oldAccessToken")
//                    Log.i(com.example.graduationproject.ui.activities.TAG, "TOKEN refresh token: $refreshToken")
//                    getNewAccessToken(oldAccessToken, refreshToken)
//                }
//            } else {
//                startActivity(Intent(this, MainActivity::class.java))
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                finish()
//            }
//        }
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    private fun convertServerTimeToTimestamp(serverTime: String): Long {
//        if (serverTime.isEmpty()) {
//            return 0
//        }
//        val dateOnly = serverTime.substring(5, 25)
//        val formatter: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
//        val date: Date = formatter.parse(dateOnly) as Date
//        return date.time / 1000
//    }
//
//    private fun isAccessTokenExpired(accessTokenTimestamp: Long): Boolean {
//        val currentTimeInSeconds = System.currentTimeMillis() / 1000
//        Log.i(com.example.graduationproject.ui.activities.TAG, "ISLAM isAccessTokenExpired: Current time $currentTimeInSeconds")
//        Log.i(com.example.graduationproject.ui.activities.TAG, "ISLAM isAccessTokenExpired: Expiration time  $accessTokenTimestamp")
//        Log.i(com.example.graduationproject.ui.activities.TAG, "ISLAM isAccessTokenExpired: Differenece time  ${accessTokenTimestamp - currentTimeInSeconds}")
//        return currentTimeInSeconds - accessTokenTimestamp >= 0
////        return (currentTimeInSeconds - (accessTokenTimestamp + 7200)) >= 0
//    }
//
//    private fun isRefreshTokenExpired(refreshTokenTimestamp: Long): Boolean {
//        val currentTimeInSeconds = System.currentTimeMillis() / 1000
//        return (currentTimeInSeconds - (refreshTokenTimestamp + 7200)) >= 0
//    }
//
//    private fun getNewAccessToken(oldAccessToken: String, oldRefreshToken: String) {
//        // Toast.makeText(this, "I am getting new access token", Toast.LENGTH_SHORT).show()
//        val refreshTokenObj = RefreshToken(oldAccessToken, oldRefreshToken)
//        lifecycleScope.launch {
//            val token = splashActivityViewModel.refreshToken(refreshTokenObj)
//            if (token != null) {
//
//                val accessToken = token.access_token
//                val refreshToken = token.refresh_token
//                val accessTokenExTime = token.access_token_exp
//                val refreshTokenExTime = token.refresh_token_exp
//
//                SplashActivity.setAccessToken(this@SplashActivity, accessToken)
//                SplashActivity.setRefreshToken(this@SplashActivity, refreshToken)
//                SplashActivity.setAccessTokenExpirationTime(this@SplashActivity, accessTokenExTime)
//                SplashActivity.setRefreshTokenExpirationTime(
//                    this@SplashActivity,
//                    refreshTokenExTime
//                )
//                Log.i(
//                    com.example.graduationproject.ui.activities.TAG, "ISLAM onCreate: ${
//                        SplashActivity.getAccessToken(
//                            this@SplashActivity
//                        )
//                    }")
//                navigateToMainActivity()
//
//            }
//        }
//    }
//
//    private fun navigateToMainActivity() {
//        startActivity(Intent(this, MainActivity::class.java))
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//        finish()
//    }


}