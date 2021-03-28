package com.example.graduationproject.ui.fragments

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.model.authentication.SignUp
import com.example.graduationproject.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.fragment_up_sign.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val TAG = "SignUpFragment"

const val USER_EMAIL = "user email"
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
        }
        else if(!isEmailValid(email)){
            Toast.makeText(requireContext(), "Enter valid email", Toast.LENGTH_SHORT)
                .show()
        }
        else if (password != confirmPassword){
            Toast.makeText(requireContext(), "Passwords don't match", Toast.LENGTH_SHORT).show()
        }
        else {
            saveEmailInPrefs(requireContext(), email)
            val signUp = SignUp(firstName, lastName, email, password)
            lifecycleScope.launch {
               val responseMessage = signUpViewModel.signUp(signUp)
               if(responseMessage != null){
                   Toast.makeText(
                       requireContext(),
                       "Account created successfully",
                       Toast.LENGTH_SHORT
                   ).show()
                   navigateToVerificationFragment()
               }
            }

        }
    }

    private fun navigateToVerificationFragment(){
        val action = SignUpFragmentDirections.actionSignUpFragmentToVerificationFragment()
        findNavController().navigate(action)
    }

    companion object{
        fun saveEmailInPrefs(context: Context?, userEmail: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(USER_EMAIL, userEmail)
                .apply()
        }

        fun getEmailFromPrefs(context: Context?): String? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(USER_EMAIL, "")
        }
    }

    private fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

}
