package com.example.graduationproject.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.model.authentication.RefreshToken
import com.example.graduationproject.viewmodel.SplashActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat

import java.text.SimpleDateFormat
import java.util.*

const val accessTokenExpirationTime = "Sun, 27 Dec 2020 15:42:25 GMT"
const val refreshTokenExpirationTime = "Sat, 12 Dec 2020 15:27:25 GMT"
const val MINUTES_15 = 900
const val DAYS_14 = 1209600


private const val SIGNED_UP_VERIFIED_SIGNED_IN = "signed up verified signed in"
private const val WELCOMED = "welcomed"
private const val ACCESS_TOKEN = "access token"
private const val ACCESS_TOKEN_EX_TIME = "access token expiration time"
private const val REFRESH_TOKEN = "refresh token"
private const val REFRESH_TOKEN_EX_TIME = "refresh token expiration time"

private const val TAG = "SplashActivity"

class SplashActivity : AppCompatActivity() {
    private val splashActivityViewModel by viewModel<SplashActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_splash)



        val signedInORSignedUpVerified = getSignedIn(this)
        val welcomed = getWelcomed(this)

        val accessTokenExTime = getAccessTokenExpirationTime(this).orEmpty()
        val refreshTokenExTime = getRefreshTokenExpirationTime(this).orEmpty()

        Handler().postDelayed({
            when {
                signedInORSignedUpVerified -> {
                    tokenizeUser(accessTokenExTime, refreshTokenExTime)
                }
                welcomed -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
                else -> {
                    setWelcomed(this, true)
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            }
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()

        }, 2000)
    }

    private fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        supportActionBar?.hide()
    }

    companion object {
        fun getSignedIn(context: Context?): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getBoolean(SIGNED_UP_VERIFIED_SIGNED_IN, false)
        }

        fun getWelcomed(context: Context?): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getBoolean(WELCOMED, false)
        }

        fun setSignedIn(context: Context?, state: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SIGNED_UP_VERIFIED_SIGNED_IN, state)
                .apply()
        }

        fun setWelcomed(context: Context?, state: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(WELCOMED, state)
                .apply()
        }

        fun setAccessToken(context: Context?, accessToken: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(ACCESS_TOKEN, accessToken)
                .apply()
        }

        fun setAccessTokenExpirationTime(context: Context?, accessTokenExTime: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(ACCESS_TOKEN_EX_TIME, accessTokenExTime)
                .apply()
        }

        fun getAccessToken(context: Context?): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(ACCESS_TOKEN, "")
        }

        fun getAccessTokenExpirationTime(context: Context?): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(ACCESS_TOKEN_EX_TIME, "")
        }

        fun setRefreshToken(context: Context?, refreshToken: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(REFRESH_TOKEN, refreshToken)
                .apply()
        }

        fun setRefreshTokenExpirationTime(context: Context?, refreshTokenExTime: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(REFRESH_TOKEN_EX_TIME, refreshTokenExTime)
                .apply()
        }

        fun getRefreshToken(context: Context?): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(REFRESH_TOKEN, "")
        }

        fun getRefreshTokenExpirationTime(context: Context?): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(REFRESH_TOKEN_EX_TIME, "")
        }


    }

    private fun tokenizeUser(accessTokenExTime: String?, refreshTokenExTime: String?) {
        val accessTokenExTimestamp = convertServerTimeToTimestamp(accessTokenExTime.orEmpty())
        val refreshTokenExTimestamp = convertServerTimeToTimestamp(refreshTokenExTime.orEmpty())

        Toast.makeText(this, "${isAccessTokenExpired(accessTokenExTimestamp)}", Toast.LENGTH_SHORT)
            .show()
        if (isAccessTokenExpired(accessTokenExTimestamp)) {
            if (isRefreshTokenExpired(refreshTokenExTimestamp)) {
                Toast.makeText(this, "Navigate to Login", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, RegisterActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            } else {
                val oldAccessToken = getAccessToken(this).toString()
                val refreshToken = getRefreshToken(this).toString()
                Log.i(TAG, "TOKEN old access token: $oldAccessToken")
                Log.i(TAG, "TOKEN refresh token: $refreshToken")
                getNewAccessToken(oldAccessToken, refreshToken)
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertServerTimeToTimestamp(serverTime: String): Long {
        if (serverTime.isEmpty()) {
            return 0
        }
        val dateOnly = serverTime.substring(5, 25)
        val formatter: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
        val date: Date = formatter.parse(dateOnly) as Date
        return date.time / 1000
    }

    private fun isAccessTokenExpired(accessTokenTimestamp: Long): Boolean {
        val currentTimeInSeconds = System.currentTimeMillis() / 1000
        Log.i(TAG, "ISLAM isAccessTokenExpired: Current time $currentTimeInSeconds")
        Log.i(TAG, "ISLAM isAccessTokenExpired: Expiration time  ${accessTokenTimestamp}")
        Log.i(TAG, "ISLAM isAccessTokenExpired: Differenece time  ${accessTokenTimestamp - currentTimeInSeconds}")
        return currentTimeInSeconds - accessTokenTimestamp >= 0
//        return (currentTimeInSeconds - (accessTokenTimestamp + 7200)) >= 0
    }

    private fun isRefreshTokenExpired(refreshTokenTimestamp: Long): Boolean {
        val currentTimeInSeconds = System.currentTimeMillis() / 1000
        return (currentTimeInSeconds - (refreshTokenTimestamp + 7200)) >= 0
    }

    private fun getNewAccessToken(oldAccessToken: String, oldRefreshToken: String) {
        // Toast.makeText(this, "I am getting new access token", Toast.LENGTH_SHORT).show()
        val refreshTokenObj = RefreshToken(oldAccessToken, oldRefreshToken)
        lifecycleScope.launch {
            val token = splashActivityViewModel.refreshToken(refreshTokenObj)
            if (token != null) {

                val accessToken = token.access_token
                val refreshToken = token.refresh_token
                val accessTokenExTime = token.access_token_exp
                val refreshTokenExTime = token.refresh_token_exp

                setAccessToken(this@SplashActivity, accessToken)
                setRefreshToken(this@SplashActivity, refreshToken)
                setAccessTokenExpirationTime(this@SplashActivity, accessTokenExTime)
                setRefreshTokenExpirationTime(this@SplashActivity, refreshTokenExTime)
                Log.i(TAG, "ISLAM onCreate: ${getAccessToken(this@SplashActivity)}")
                navigateToMainActivity()

            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }


}