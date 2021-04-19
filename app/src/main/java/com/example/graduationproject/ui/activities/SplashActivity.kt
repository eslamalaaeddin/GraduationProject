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
import com.example.graduationproject.helpers.Constants.ACCESS_TOKEN
import com.example.graduationproject.helpers.Constants.ACCESS_TOKEN_EX_TIME
import com.example.graduationproject.helpers.Constants.APP_PREFS
import com.example.graduationproject.helpers.Constants.LOGGED_OUT
import com.example.graduationproject.helpers.Constants.REFRESH_TOKEN
import com.example.graduationproject.helpers.Constants.REFRESH_TOKEN_EX_TIME
import com.example.graduationproject.helpers.Constants.SEARCH_METHOD
import com.example.graduationproject.helpers.Constants.SIGNED_UP_VERIFIED_SIGNED_IN
import com.example.graduationproject.helpers.Constants.USER_EMAIL
import com.example.graduationproject.helpers.Constants.USER_ID
import com.example.graduationproject.helpers.Constants.USER_IMAGE_URL
import com.example.graduationproject.helpers.Constants.USER_NAME
import com.example.graduationproject.helpers.Constants.WELCOMED
import com.example.graduationproject.models.authentication.RefreshToken
import com.example.graduationproject.viewmodels.SplashActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat

import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SplashActivity"

class SplashActivity : AppCompatActivity() {
    private val splashActivityViewModel by viewModel<SplashActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_splash)

        val signedInORSignedUpVerified = getSignedIn(this)
        val welcomed = getWelcomed(this)
        val loggedOut  = getLoggedOut(this)


        Handler().postDelayed({
            when {
                loggedOut -> {
//                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, RegisterActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }

                signedInORSignedUpVerified -> {
//                    tokenizeUser(accessTokenExTime, refreshTokenExTime)
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
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


    }




}