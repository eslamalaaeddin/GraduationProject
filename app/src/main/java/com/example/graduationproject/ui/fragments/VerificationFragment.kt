package com.example.graduationproject.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.FragmentInSignBinding
import com.example.graduationproject.databinding.FragmentVerificationBinding
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.models.authentication.Verify
import com.example.graduationproject.ui.activities.MainActivity
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodels.NavigationDrawerViewModel
import com.example.graduationproject.viewmodels.VerificationFragmentViewModel
import kotlinx.android.synthetic.main.fragment_verification.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "VerificationFragment"

class VerificationFragment : Fragment() {
    private val verificationFragmentViewModel by viewModel<VerificationFragmentViewModel>()
    private val navigationDrawerViewModel by viewModel<NavigationDrawerViewModel>()
    private val cachingViewModel by viewModel<CachingViewModel>()
    private lateinit var bindingInstance: FragmentVerificationBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_verification,
            container,
            false
        )
        return bindingInstance.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        noVerificationSendTextView.setOnClickListener {
            Toast.makeText(
                context,
                "Verification code has been sent,please check your phone",
                Toast.LENGTH_LONG
            ).show()
        }

//        haveAccountTextView.setOnClickListener {
//            val action = VerificationFragmentDirections.actionVerificationFragmentToSignInFragment()
//            findNavController().navigate(action)
//        }

        verifyButton.setOnClickListener {
            val verificationCode = verification_code_edit_text.text.toString()
            validateVerificationCodeAndNavigateToMainActivity(verificationCode)
        }
    }

    private fun validateVerificationCodeAndNavigateToMainActivity(code: String) {
        if (code.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.no_verification_code_entered), Toast.LENGTH_SHORT)
                .show()
        } else {
            val email = SplashActivity.getEmailFromPrefs(requireContext())
            Log.i(TAG, "AHMAD validateVerificationCodeAndNavigateToMainActivity: $code")
            val verify = Verify(email, code.trim().toInt())
            Log.i(TAG, "PPPP: $verify")
            bindingInstance.progressBar.visibility = View.VISIBLE
            bindingInstance.verifyButton.isEnabled = false

            lifecycleScope.launch {
                val token = verificationFragmentViewModel.verifyUser(verify)
                token?.let { t ->
                    val accessToken = t.access_token
                    val refreshToken = t.refresh_token
                    val accessTokenExTime = t.access_token_exp
                    val refreshTokenExTime = t.refresh_token_exp

                    val user = navigationDrawerViewModel.getUser(accessToken)
                    user?.let {
                        val userId: Long = it.id ?: 0
                        Log.i(TAG, "qqqqq validateVerificationCodeAndNavigateToMainActivity: $user")
                        Log.i(
                            TAG,
                            "qqqqq validateVerificationCodeAndNavigateToMainActivity: $userId"
                        )
                        saveUserAsLoggedInAndSaveTokens(
                            accessToken,
                            refreshToken,
                            accessTokenExTime,
                            refreshTokenExTime,
                            userId,
                            "${user.firstName} ${user.lastName}",
                            user.image.orEmpty(),
                            email.orEmpty()
                        )
                        bindingInstance.progressBar.visibility = View.GONE
                        bindingInstance.verifyButton.isEnabled = true

//                        lifecycleScope.launch {
//                            cachingViewModel.insertUser(it)
//                        }
                        navigateToMainActivity()
                    }

                }
                if (token == null) {
                    bindingInstance.progressBar.visibility = View.GONE
                    bindingInstance.verifyButton.isEnabled = true
                }
            }
            dismissProgressAfterTimeOut()

        }
    }

    private fun saveUserAsLoggedInAndSaveTokens(
        accessToken: String,
        refreshToken: String,
        accessTokenExTime: String,
        refreshTokenExTime: String,
        userId: Long,
        userName: String,
        userImageUrl: String,
        email: String
    ) {
        SplashActivity.setAccessToken(requireContext(), accessToken)
        SplashActivity.setRefreshToken(requireContext(), refreshToken)
        SplashActivity.setAccessTokenExpirationTime(requireContext(), accessTokenExTime)
        SplashActivity.setRefreshTokenExpirationTime(requireContext(), refreshTokenExTime)
        SplashActivity.setUserId(requireContext(), userId)
        SplashActivity.setUserName(requireContext(), userName)
        SplashActivity.setUserImageUrl(requireContext(), userImageUrl)
        SplashActivity.setSignedIn(requireContext(), true)
        SplashActivity.setLoggedOut(requireContext(), false)
        SplashActivity.saveEmailInPrefs(requireContext(), email)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        finishAffinity(requireActivity())
        activity?.finish()
    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            Handler().postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
                bindingInstance.verifyButton.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }
}