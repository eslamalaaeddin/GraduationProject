package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentInSignBinding
import com.example.graduationproject.databinding.FragmentUpSignBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.model.authentication.SignUp
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.fragment_up_sign.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val TAG = "SignUpFragment"

class SignUpFragment : Fragment() {
    private val signUpViewModel by viewModel<SignUpViewModel>()
    private lateinit var bindingInstance: FragmentUpSignBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_up_sign,
            container,
            false
        )
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        haveAccountTextView.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }

        signUpButton.setOnClickListener {
            val firstName = bindingInstance.firstNameEditText.text.toString()
            val lastName = bindingInstance.lastNameEditText.text.toString()
            val email = bindingInstance.mailEditText.text.toString()
            val password = bindingInstance.passwordEditText.text.toString()
            val confirmPassword = bindingInstance.confirmPasswordEditText.text.toString()

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

        if (firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
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

        else if (password.trim().count() < 6 || confirmPassword.trim().count() < 6){
            Toast.makeText(requireContext(), "Passwords must be at least 6 characters", Toast.LENGTH_SHORT).show()
        }

        else {
            val signUp = SignUp(firstName, lastName, email, password)
            lifecycleScope.launch {
                bindingInstance.progressBar.visibility = View.VISIBLE
                bindingInstance.signUpButton.isEnabled = false
                SplashActivity.saveEmailInPrefs(requireContext(), email)
               val responseMessage = signUpViewModel.signUp(signUp)
               if(responseMessage != null){
                   Toast.makeText(
                       requireContext(),
                       "Account created successfully",
                       Toast.LENGTH_SHORT
                   ).show()
                   navigateToSignInFragment()

               }
                else{
                   bindingInstance.progressBar.visibility = View.GONE
                   bindingInstance.signUpButton.isEnabled = true
                }
            }
            dismissProgressAfterTimeOut()
        }
    }

    private fun navigateToSignInFragment(){
        val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
        findNavController().navigate(action)
    }

    override fun onStop() {
        super.onStop()
        bindingInstance.progressBar.visibility = View.GONE
        bindingInstance.signUpButton.isEnabled = true
    }

    private fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            Handler().postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
                bindingInstance.signUpButton.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }

    }



}
