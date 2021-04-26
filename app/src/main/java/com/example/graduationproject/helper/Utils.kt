package com.example.graduationproject.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.graduationproject.helper.Constants.ACCESS_TOKEN
import com.example.graduationproject.helper.Constants.ACCESS_TOKEN_EX_TIME
import com.example.graduationproject.helper.Constants.APP_PREFS
import com.example.graduationproject.helper.Constants.LOGGED_OUT
import com.example.graduationproject.helper.Constants.REFRESH_TOKEN
import com.example.graduationproject.helper.Constants.REFRESH_TOKEN_EX_TIME
import com.example.graduationproject.helper.Constants.SEARCH_METHOD
import com.example.graduationproject.helper.Constants.SIGNED_UP_VERIFIED_SIGNED_IN
import com.example.graduationproject.helper.Constants.USER_EMAIL
import com.example.graduationproject.helper.Constants.USER_ID
import com.example.graduationproject.helper.Constants.USER_IMAGE_URL
import com.example.graduationproject.helper.Constants.USER_NAME
import com.example.graduationproject.helper.Constants.WELCOMED

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

    fun getSignedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getBoolean(SIGNED_UP_VERIFIED_SIGNED_IN, false)
    }

    fun getWelcomed(context: Context): Boolean {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getBoolean(WELCOMED, false)
    }

    fun setSignedIn(context: Context, state: Boolean) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putBoolean(SIGNED_UP_VERIFIED_SIGNED_IN, state)
            .apply()
    }

    fun setLoggedOut(context: Context, state: Boolean) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putBoolean(LOGGED_OUT, state)
            .apply()
    }

    fun getLoggedOut(context: Context): Boolean {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getBoolean(LOGGED_OUT, false)
    }

    fun setWelcomed(context: Context, state: Boolean) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putBoolean(WELCOMED, state)
            .apply()
    }

    fun setAccessToken(context: Context, accessToken: String) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putString(ACCESS_TOKEN, accessToken)
            .apply()
    }

    fun setUserId(context: Context, userId: Long) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putLong(USER_ID, userId)
            .apply()
    }

    fun setUserName(context: Context, userName: String) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putString(USER_NAME, userName)
            .apply()
    }

    fun setUserImageUrl(context: Context, userImageUrl: String) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putString(USER_IMAGE_URL, userImageUrl)
            .apply()
    }

    fun getUserId(context: Context): Long {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getLong(USER_ID, 0)
    }

    fun setSearchMethod(context: Context, searchMethod: String) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putString(SEARCH_METHOD, searchMethod)
            .apply()
    }

    fun getSearchMethod(context: Context): String? {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getString(SEARCH_METHOD,"name" )
    }

    fun getUserName(context: Context): String? {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getString(USER_NAME,"" )
    }

    fun getUserImageUrl(context: Context): String? {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getString(USER_IMAGE_URL,"user.png" )
    }

    fun saveEmailInPrefs(context: Context, userEmail: String) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putString(USER_EMAIL, userEmail)
            .apply()
    }

    fun getEmailFromPrefs(context: Context): String? {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getString(USER_EMAIL, "")
    }

    fun setAccessTokenExpirationTime(context: Context, accessTokenExTime: String) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putString(ACCESS_TOKEN_EX_TIME, accessTokenExTime)
            .apply()
    }

    fun getAccessToken(context: Context): String? {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getString(ACCESS_TOKEN, "")
    }

    fun getAccessTokenExpirationTime(context: Context): String? {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getString(ACCESS_TOKEN_EX_TIME, "")
    }

    fun setRefreshToken(context: Context, refreshToken: String) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putString(REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun setRefreshTokenExpirationTime(context: Context, refreshTokenExTime: String) {
        context.getSharedPreferences(APP_PREFS, 0)
            .edit()
            .putString(REFRESH_TOKEN_EX_TIME, refreshTokenExTime)
            .apply()
    }

    fun getRefreshToken(context: Context): String? {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getString(REFRESH_TOKEN, "")
    }

    fun getRefreshTokenExpirationTime(context: Context): String? {
        val prefs = context.getSharedPreferences(APP_PREFS, 0)
        return prefs.getString(REFRESH_TOKEN_EX_TIME, "")
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
//                    Log.i(com.example.graduationproject.ui.activities.com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "TOKEN old access token: $oldAccessToken")
//                    Log.i(com.example.graduationproject.ui.activities.com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "TOKEN refresh token: $refreshToken")
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
//        Log.i(com.example.graduationproject.ui.activities.com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "ISLAM isAccessTokenExpired: Current time $currentTimeInSeconds")
//        Log.i(com.example.graduationproject.ui.activities.com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "ISLAM isAccessTokenExpired: Expiration time  $accessTokenTimestamp")
//        Log.i(com.example.graduationproject.ui.activities.com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "ISLAM isAccessTokenExpired: Differenece time  ${accessTokenTimestamp - currentTimeInSeconds}")
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
//                    com.example.graduationproject.ui.activities.com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "ISLAM onCreate: ${
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