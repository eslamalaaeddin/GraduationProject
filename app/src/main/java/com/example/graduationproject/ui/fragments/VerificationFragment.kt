package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.model.authentication.Verify
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.ui.activities.SplashActivity
import kotlinx.android.synthetic.main.fragment_verification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "VerificationFragment"

class VerificationFragment : Fragment(R.layout.fragment_verification) {
    private val api = RetrofitInstance.api
    //val args: VerificationFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noVerificationSendTextView.setOnClickListener {
            Toast.makeText(
                context,
                "Verification code has been sent,please check your phone",
                Toast.LENGTH_LONG
            ).show()
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

            val call = api.verify(verify)
            call.enqueue(object : Callback<Token> {
                override fun onResponse(
                    call: Call<Token>,
                    response: Response<Token>
                ) {
                    val responseCode = response.code()
                    //Not acceptable
                    if (responseCode == 406) {
                        Toast.makeText(requireContext(), "Wrong code", Toast.LENGTH_SHORT).show()
                    } else if (responseCode == 200) {
                        Log.i(TAG, "ISLAM onResponse: ${response.body()}")
                        val accessToken = response.body()?.access_token.orEmpty()
                        val refreshToken = response.body()?.refresh_token.orEmpty()
                        SplashActivity.setAccessToken(requireContext(), accessToken)
                        SplashActivity.setRefreshToken(requireContext(), refreshToken)
                        SplashActivity.setSignedIn(requireContext(), true)
                        val action = VerificationFragmentDirections.actionVerificationFragmentToMainActivity()
                        findNavController().navigate(action)
                        activity?.finish()
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