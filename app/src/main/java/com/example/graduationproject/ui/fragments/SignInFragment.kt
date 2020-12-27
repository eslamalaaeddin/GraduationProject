package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.model.authentication.Login
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.ui.activities.SplashActivity
import kotlinx.android.synthetic.main.fragment_in_sign.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SignInFragment"
class SignInFragment : Fragment(R.layout.fragment_in_sign ) {
    private val api = RetrofitInstance.api

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
            val call = api.login(login)
            call.enqueue(object : Callback<Token> {
                override fun onResponse(
                    call: Call<Token>,
                    response: Response<Token>
                ) {
                    
                    when (response.code()) {
                        404 -> Toast.makeText(requireContext(), "No account matches this email", Toast.LENGTH_SHORT).show()
                        403 -> Toast.makeText(requireContext(), "Unverified account", Toast.LENGTH_SHORT).show()
                        406 -> Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()
                        200 -> {
                            val accessToken = response.body()?.access_token.orEmpty()
                            val refreshToken = response.body()?.refresh_token.orEmpty()
                            Log.i(TAG, "onResponse: ISLAM ${response.body()}")
                            SplashActivity.setAccessToken(requireContext(), accessToken)
                            SplashActivity.setRefreshToken(requireContext(), refreshToken)
                            SplashActivity.setSignedIn(requireContext(), true)
                            val action = SignInFragmentDirections.actionSignInFragmentToMainActivity()
                            findNavController().navigate(action)
                            activity?.finish()
                        }
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }
}