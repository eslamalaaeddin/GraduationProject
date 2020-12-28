package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.SignUp
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.fragment_up_sign.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SignUpFragment"

class SignUpFragment : Fragment(R.layout.fragment_up_sign) {
    private val signUpViewModel by viewModel<SignUpViewModel>()
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

            validateUserAndNavigateToVerificationFragment(
                firstName,
                lastName,
                email,
                password,
                confirmPassword
            )

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
        } else {
            val signUp = SignUp(firstName, lastName, email, password)
            lifecycleScope.launch {
               val responseMessage = signUpViewModel.signUp(signUp)
//               if(responseMessage != null){
                   Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                   navigateToVerificationFragment()
//               }
            }

        }
    }

    private fun navigateToVerificationFragment(){
        val action = SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment()
        findNavController().navigate(action)
    }

}
