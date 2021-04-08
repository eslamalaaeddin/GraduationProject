package com.example.graduationproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.NavigationDrawerViewModel
import com.example.graduationproject.viewmodel.SplashActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "TokenActivity"
class TokenActivity : AppCompatActivity() {
    //to get access to refresh token method
    private val splashViewModel : SplashActivityViewModel by viewModel()
    //to make the dummy request that check if the token is expired
    private val userViewModel : NavigationDrawerViewModel by viewModel()

    private lateinit var accessToken: String
    private lateinit var requestButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
        requestButton = findViewById(R.id.requestButton)
        accessToken = SplashActivity.getAccessToken(this).orEmpty()

        requestButton.setOnClickListener {
            tryToGetUser(accessToken)
            lifecycleScope.launch{
                val user = userViewModel.getUser(accessToken)
                if (user == null){
                    Log.i(TAG, "ISLAM tryToGetUser: REFRESH THE TOKEN")
                }
                Log.i(TAG, "ISLAM tryToGetUser: $user")
            }
        }


    }

    private fun tryToGetUser(accessToken: String) {
        userViewModel.getUserWithViewModelScope(accessToken).observe(this){user ->
            if (user == null){
                Log.i(TAG, "ISLAM tryToGetUser: REFRESH THE TOKEN")
            }
            Log.i(TAG, "ISLAM tryToGetUser: $user")
        }
    }
}