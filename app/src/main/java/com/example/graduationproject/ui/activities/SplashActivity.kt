package com.example.graduationproject.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.helper.Utils.getLoggedOut
import com.example.graduationproject.helper.Utils.getSignedIn
import com.example.graduationproject.helper.Utils.getWelcomed
import com.example.graduationproject.helper.Utils.setWelcomed

private const val TAG = "SplashActivity"

class SplashActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_splash)

        val signedInORSignedUpVerified = getSignedIn(this)
        val welcomed = getWelcomed(this)
        val loggedOut = getLoggedOut(this)

        handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            when {
                loggedOut -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }

                signedInORSignedUpVerified -> {
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

}