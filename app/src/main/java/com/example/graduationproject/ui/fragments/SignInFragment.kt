package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.FragmentInSignBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Utils.getEmailFromPrefs
import com.example.graduationproject.helper.Utils.saveEmailInPrefs
import com.example.graduationproject.helper.Utils.setAccessToken
import com.example.graduationproject.helper.Utils.setAccessTokenExpirationTime
import com.example.graduationproject.helper.Utils.setLoggedOut
import com.example.graduationproject.helper.Utils.setRefreshToken
import com.example.graduationproject.helper.Utils.setRefreshTokenExpirationTime
import com.example.graduationproject.helper.Utils.setSignedIn
import com.example.graduationproject.helper.Utils.setUserId
import com.example.graduationproject.helper.Utils.setUserImageUrl
import com.example.graduationproject.helper.Utils.setUserName
import com.example.graduationproject.models.authentication.Login
import com.example.graduationproject.viewmodels.LoginViewModel
import com.example.graduationproject.viewmodels.NavigationDrawerViewModel
import kotlinx.android.synthetic.main.fragment_in_sign.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "SignInFragment"

class SignInFragment : Fragment() {
    private val loginViewModel by viewModel<LoginViewModel>()
    private val navigationDrawerViewModel by viewModel<NavigationDrawerViewModel>()
    private val cachingViewModel by viewModel<CachingViewModel>()
    private lateinit var bindingInstance: FragmentInSignBinding
    private lateinit var loadingHandler: Handler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_in_sign,
            container,
            false
        )
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingHandler = Handler(Looper.getMainLooper())

        bindingInstance.mailEditText.setText(getEmailFromPrefs(requireContext()))

        doNotHaveAccountTextView.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        forgetPasswordTextView.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToForgetPasswordFragment()
            findNavController().navigate(action)
        }

        signInButton.setOnClickListener {
            val email = bindingInstance.mailEditText.text.toString()
            val password = bindingInstance.passwordEditText.text.toString()
            validateUserAndNavigateToMainActivity(email, password)
        }

    }

    private fun validateUserAndNavigateToMainActivity(mail: String, password: String) {
        if (mail.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.all_information_first), Toast.LENGTH_SHORT)
                .show()
        }
        else {

            val login = Login(mail, password)
            lifecycleScope.launch {
                bindingInstance.progressBar.visibility = View.VISIBLE
                bindingInstance.signInButton.isEnabled = false
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
                            "${user.firstName} ${user.lastName}",
                            user.image.orEmpty(),
                            mail
                        )

//                        lifecycleScope.launch {
//                            cachingViewModel.insertUser(it)
//                        }
                        navigateToMainActivity()
                    }
                    if (user == null){
                        bindingInstance.progressBar.visibility = View.GONE
                        bindingInstance.signInButton.isEnabled = true
                    }

                }
                if (token == null){
                    bindingInstance.progressBar.visibility = View.GONE
                    bindingInstance.signInButton.isEnabled = true
                }
            }
            dismissProgressAfterTimeOut()
        }
    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            loadingHandler.postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
                bindingInstance.signInButton.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }

    override fun onStop() {
        super.onStop()
        bindingInstance.progressBar.visibility = View.GONE
        bindingInstance.signInButton.isEnabled = true
    }

    private fun saveUserAsLoggedInAndSaveTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExTime: String,
        refreshTokenExTime: String,
        userId: Long,
        userName: String,
        userImageUrl : String,
        mail: String
    ) {
        setAccessToken(requireContext(), accessToken)
        setRefreshToken(requireContext(), refreshToken)
        setAccessTokenExpirationTime(requireContext(), accessTokenExTime)
        setRefreshTokenExpirationTime(requireContext(), refreshTokenExTime)
        setUserId(requireContext(), userId)
        setUserName(requireContext(), userName)
        setUserImageUrl(requireContext(), userImageUrl)
        saveEmailInPrefs(requireContext(), mail)
        setSignedIn(requireContext(), true)
        setLoggedOut(requireContext(), false)
    }

    private fun navigateToMainActivity() {
        bindingInstance.progressBar.visibility = View.GONE
        val action = SignInFragmentDirections.actionSignInFragmentToMainActivity()
        findNavController().navigate(action)
        activity?.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingHandler.removeCallbacksAndMessages(null)
    }

}