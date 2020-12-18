package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.SignUp
import com.example.graduationproject.network.RetrofitInstance
import kotlinx.android.synthetic.main.fragment_up_sign.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SignUpFragment"

class SignUpFragment : Fragment(R.layout.fragment_up_sign) {
    private val api = RetrofitInstance.api
    val scope = CoroutineScope(Dispatchers.IO)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        haveAccountTextView.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }

        signUpButton.setOnClickListener {
            val firstName = first_name_edit_text.text.toString()
            val lastName = last_name_edit_text.text.toString()
            val email = mail_edit_text.text.toString()
            val password = password_edit_text.text.toString()
            val confirmPassword = confirm_password_edit_text.text.toString()

            validateUserAndNavigateToVerificationFragment(firstName, lastName, email, password, confirmPassword)

        }
    }

    private fun validateUserAndNavigateToVerificationFragment(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Enter all information first", Toast.LENGTH_SHORT)
                .show()
        }
        else {
            val signUp = SignUp(firstName, lastName, email, password)
            val response = api.signUp(signUp)
            response.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    val code = response.code()
                    Log.i(TAG, "ISLAM onResponse: ${response.code()}")
                    Log.i(TAG, "ISLAM onResponse: ${response.message()}")
                    Log.i(TAG, "ISLAM onResponse: ${response.raw()}")
                    Log.i(TAG, "ISLAM onResponse: ${response.body()}")


                    if (code == 302) {
                        Toast.makeText(requireContext(), "This user already exists", Toast.LENGTH_SHORT).show()
                    }
                    else if (code == 201 || code == 500){
                        //Toast.makeText(context, "Verification code has been sent, please check your phone.", Toast.LENGTH_LONG).show()
                        val action =
                            SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment()
                        findNavController().navigate(action)
                    }

                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })

        }
    }
}