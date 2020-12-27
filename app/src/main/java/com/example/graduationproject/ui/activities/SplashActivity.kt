package com.example.graduationproject.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.Window
import android.view.WindowManager
import com.example.graduationproject.R

private const val SIGNED_UP_VERIFIED_SIGNED_IN = "signed up verified signed in"
private const val WELCOMED = "welcomed"
private const val ACCESS_TOKEN = "access token"
private const val REFRESH_TOKEN = "refresh token"

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        makeFullScreen()

        setContentView(R.layout.activity_splash)
        var signedInORSignedUpVerified = getSignedIn(this)
        var welcomed = getWelcomed(this)


        Handler().postDelayed({
//            if (signedInORSignedUpVerified) {
//                startActivity(Intent(this, MainActivity::class.java))
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                finish()
//            } else if (welcomed) {
//                startActivity(Intent(this, RegisterActivity::class.java))
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                finish()
//            }else{
//                setWelcomed(this, true)
//                startActivity(Intent(this, WelcomeActivity::class.java))
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                finish()
//            }

            startActivity(Intent(this, RegisterActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()


        }, 1000)
    }

    private fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        supportActionBar?.hide()
    }

    companion object{
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

        fun getAccessToken(context: Context?): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(ACCESS_TOKEN, "")
        }

        fun setRefreshToken(context: Context?, refreshToken: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(REFRESH_TOKEN, refreshToken)
                .apply()
        }

        fun getRefreshToken(context: Context?): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(REFRESH_TOKEN, "")
        }
    }




}