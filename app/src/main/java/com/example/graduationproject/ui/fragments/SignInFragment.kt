package com.example.graduationproject.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.model.authentication.Login
import com.example.graduationproject.model.authentication.RefreshToken
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.model.authentication.Verify
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.ui.activities.MainActivity
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.LoginViewModel
import com.example.graduationproject.viewmodel.NavigationDrawerViewModel
import kotlinx.android.synthetic.main.fragment_in_sign.*
import kotlinx.android.synthetic.main.fragment_password_forget.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SignInFragment"

class SignInFragment : Fragment(R.layout.fragment_in_sign) {
    private val loginViewModel by viewModel<LoginViewModel>()
    private val navigationDrawerViewModel by viewModel<NavigationDrawerViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mail_edit_text.setText(SplashActivity.getEmailFromPrefs(requireContext()))

        doNotHaveAccountTextView.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        forgetPasswordTextView.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToForgetPasswordFragment()
            findNavController().navigate(action)
        }

        signInButton.setOnClickListener {
            val email = mail_edit_text.text.toString()
            val password = password_edit_text.text.toString()
            validateUserAndNavigateToMainActivity(email, password)
        }

    }

    private fun validateUserAndNavigateToMainActivity(mail: String, password: String) {
        if (mail.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Enter all information first", Toast.LENGTH_SHORT)
                .show()
        } else {

            val login = Login(mail, password)
            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE
                val token = loginViewModel.login(login)
                token?.let { t ->
                    val accessToken = t.access_token
                    val refreshToken = t.refresh_token
                    val accessTokenExTime = t.access_token_exp
                    val refreshTokenExTime = t.refresh_token_exp

                    val user = navigationDrawerViewModel.getUser(accessToken)
                    user?.let {
                        val userId : Long = it.id ?: 0
                        saveUserAsLoggedInAndSaveTokens(
                            accessToken,
                            refreshToken,
                            accessTokenExTime,
                            refreshTokenExTime,
                            userId,
                            mail
                        )

                        navigateToMainActivity()
                    }
                    if (user == null){
                        progressBar.visibility = View.GONE
                    }

                }
                if (token == null){
                    progressBar.visibility = View.GONE
                }
            }
            dismissProgressAfterTimeOut()
        }
    }

    private fun dismissProgressAfterTimeOut() {
        Handler().postDelayed({
            progressBar.visibility = View.GONE
        }, Constants.TIME_OUT_SECONDS)

    }

    override fun onStop() {
        super.onStop()
        progressBar.visibility = View.GONE
    }

    private fun saveUserAsLoggedInAndSaveTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExTime: String,
        refreshTokenExTime: String,
        userId: Long,
        mail: String
    ) {
        SplashActivity.setAccessToken(requireContext(), accessToken)
        SplashActivity.setRefreshToken(requireContext(), refreshToken)
        SplashActivity.setAccessTokenExpirationTime(requireContext(), accessTokenExTime)
        SplashActivity.setRefreshTokenExpirationTime(requireContext(), refreshTokenExTime)
        SplashActivity.setUserId(requireContext(), userId)
        SplashActivity.saveEmailInPrefs(requireContext(), mail)
        SplashActivity.setSignedIn(requireContext(), true)
        SplashActivity.setLoggedOut(requireContext(), false)
    }

    private fun navigateToMainActivity() {
        progressBar.visibility = View.GONE
        val action = SignInFragmentDirections.actionSignInFragmentToMainActivity()
        findNavController().navigate(action)
        activity?.finish()
    }

}