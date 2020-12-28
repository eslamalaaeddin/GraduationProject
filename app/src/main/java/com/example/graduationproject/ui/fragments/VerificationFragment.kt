package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.model.authentication.Verify
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.VerificationFragmentViewModel
import kotlinx.android.synthetic.main.fragment_verification.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "VerificationFragment"

class VerificationFragment : Fragment(R.layout.fragment_verification) {
    private val verificationFragmentViewModel by viewModel<VerificationFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noVerificationSendTextView.setOnClickListener {
            Toast.makeText(context, "Verification code has been sent,please check your phone", Toast.LENGTH_LONG).show()
        }

        haveAccountTextView.setOnClickListener {
            val action = VerificationFragmentDirections.actionVerificationFragmentToSignInFragment()
            findNavController().navigate(action)
        }

        verifyButton.setOnClickListener {
            val verificationCode = verification_code_edit_text.text.toString()
            validateVerificationCodeAndNavigateToMainActivity(verificationCode)
        }
    }

    private fun validateVerificationCodeAndNavigateToMainActivity(code: String){
        if (code.isEmpty()){
            Toast.makeText(requireContext(), "Enter verification code first", Toast.LENGTH_SHORT).show()
        }
        else {
            val email = "islamalaaeddin1998@gmail.com"
            val verify = Verify(email, code.toInt())

            lifecycleScope.launch {
                val token = verificationFragmentViewModel.verifyUser(verify)
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
        val action = VerificationFragmentDirections.actionVerificationFragmentToMainActivity()
        findNavController().navigate(action)
        activity?.finish()
    }
}