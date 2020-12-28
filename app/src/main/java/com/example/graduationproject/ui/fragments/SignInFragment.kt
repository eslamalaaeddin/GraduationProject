package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.model.authentication.Login
import com.example.graduationproject.model.authentication.RefreshToken
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_in_sign.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SignInFragment"
class SignInFragment : Fragment(R.layout.fragment_in_sign ) {
    private val loginViewModel by viewModel<LoginViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mail_edit_text.setText("islamalaaeddin1998@gmail.com")
        password_edit_text.setText("123456")

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

    private fun validateUserAndNavigateToMainActivity(mail: String, password: String){
        if (mail.isEmpty() || password.isEmpty()){
            Toast.makeText(requireContext(), "Enter all information first", Toast.LENGTH_SHORT).show()
        }
        else{
            val login = Login(mail, password)
            lifecycleScope.launch {
                val token = loginViewModel.login(login)
                token?.let { t ->
                    val accessToken = t.access_token
                    val refreshToken = t.refresh_token
                    val accessTokenExTime = t.access_token_exp
                    val refreshTokenExTime = t.refresh_token_exp
                    saveUserAsLoggedInAndSaveTokens(accessToken, refreshToken, accessTokenExTime, refreshTokenExTime)
                    navigateToMainActivity()
                }

            }
        }
    }

    private fun saveUserAsLoggedInAndSaveTokens(accessToken: String, refreshToken: String, accessTokenExTime: String, refreshTokenExTime: String){
        SplashActivity.setAccessToken(requireContext(), accessToken)
        SplashActivity.setRefreshToken(requireContext(), refreshToken)
        SplashActivity.setAccessTokenExpirationTime(requireContext(), accessTokenExTime)
        SplashActivity.setRefreshTokenExpirationTime(requireContext(), refreshTokenExTime)
        SplashActivity.setSignedIn(requireContext(), true)
    }

    private fun navigateToMainActivity(){
        val action = SignInFragmentDirections.actionSignInFragmentToMainActivity()
        findNavController().navigate(action)
        activity?.finish()
    }
}