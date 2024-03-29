package com.example.graduationproject.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.example.graduationproject.R
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.FragmentVerificationBinding
import com.example.graduationproject.helper.Constants
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
    private lateinit var loadingHandler: Handler
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

        loadingHandler = Handler(Looper.getMainLooper())

        noVerificationSendTextView.setOnClickListener {
            Toast.makeText(
                context,
                "Verification code has been sent,please check your phone",
                Toast.LENGTH_LONG
            ).show()
        }


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
            val email = getEmailFromPrefs(requireContext())
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
        setAccessToken(requireContext(), accessToken)
        setRefreshToken(requireContext(), refreshToken)
        setAccessTokenExpirationTime(requireContext(), accessTokenExTime)
        setRefreshTokenExpirationTime(requireContext(), refreshTokenExTime)
        setUserId(requireContext(), userId)
        setUserName(requireContext(), userName)
        setUserImageUrl(requireContext(), userImageUrl)
        setSignedIn(requireContext(), true)
        setLoggedOut(requireContext(), false)
        saveEmailInPrefs(requireContext(), email)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        finishAffinity(requireActivity())
        activity?.finish()
    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            loadingHandler.postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
                bindingInstance.verifyButton.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingHandler.removeCallbacksAndMessages(null)
    }
}