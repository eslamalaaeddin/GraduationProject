package com.example.graduationproject.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.graduationproject.R
import com.example.graduationproject.helper.Utils.getLoggedOut
import com.example.graduationproject.helper.Utils.getSignedIn
import com.example.graduationproject.helper.Utils.getWelcomed
import com.example.graduationproject.helper.Utils.setWelcomed

private const val TAG = "SplashActivity"

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_splash)

        val signedInORSignedUpVerified = getSignedIn(this)
        val welcomed = getWelcomed(this)
        val loggedOut = getLoggedOut(this)


        Handler(Looper.getMainLooper()).postDelayed({
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

}