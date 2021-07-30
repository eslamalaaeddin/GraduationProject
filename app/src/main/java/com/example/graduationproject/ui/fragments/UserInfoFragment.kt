package com.example.graduationproject.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.graduationproject.R
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.UserInfoFragmentBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Constants.BASE_USER_IMAGE_URL
import com.example.graduationproject.helper.Constants.SETTINGS_ACTIVITY_CODE
import com.example.graduationproject.helper.Utils
import com.example.graduationproject.helper.Utils.getAccessToken
import com.example.graduationproject.helper.Utils.getEmailFromPrefs
import com.example.graduationproject.helper.Utils.getUserId
import com.example.graduationproject.helper.Utils.getUserImageUrl
import com.example.graduationproject.helper.Utils.getUserName
import com.example.graduationproject.helper.Utils.setAccessToken
import com.example.graduationproject.helper.Utils.setLoggedOut
import com.example.graduationproject.helper.Utils.setRefreshToken
import com.example.graduationproject.ui.activities.RegisterActivity
import com.example.graduationproject.ui.activities.SettingsActivity
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodels.NavigationDrawerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "NavigationDrawerBottomS"


class UserInfoFragment : Fragment() {
    private lateinit var bindingInstance: UserInfoFragmentBinding
    private val navDrawerViewModel by viewModel<NavigationDrawerViewModel>()
    private val cachingViewModel by viewModel<CachingViewModel>()
    private lateinit var accessToken: String
    private var userId: Long = 0
    private var userName: String = ""
    private var userEmail: String = ""
    private var userImageUrl: String = ""
    private lateinit var loadingHandler: Handler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.user_info_fragment,
            container,
            false
        )
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = getAccessToken(requireContext()).toString()
        userId = getUserId(requireContext())

        loadingHandler = Handler(Looper.getMainLooper())

        bindingInstance.settingsLayout.setOnClickListener {
            navigateToSettingsActivity()
        }
        bindingInstance.logOutLayout.setOnClickListener {
            showLogoutDialog()
        }

        bindingInstance.infoLayout.setOnClickListener {
            showTeamNames()
        }

        dismissProgressAfterTimeOut()

        //OFFLINE
        if (Utils.getConnectionType(requireContext()) == 0) {
            getUserAndUpdateUiOffLine()
        }
        //ONLINE
        else {
            getUserAndUpdateUiOnLine()
        }

    }

    private fun showTeamNames(){
            // setup the alert builder
            val builder = AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
            builder.setTitle(getString(R.string.team_members))

            val names = arrayOf(
                getString(R.string.islam),
                getString(R.string.sleem),
                getString(R.string.israa),
                getString(R.string.fwzia),
                getString(R.string.khedr),
                getString(R.string.bakr),
                getString(R.string.hamis),
                getString(R.string.yomna),
                getString(R.string.magdy)
            ).toList().shuffled().toTypedArray()

        builder.setItems(names) { dialog, which ->
            Toast.makeText(requireContext(), names[which], Toast.LENGTH_LONG).show()
        }

            val dialog = builder.create()
            dialog.show()
    }


    private fun showLogoutDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.logout_dialog)

        val cancelButton = dialog.findViewById(R.id.cancelLogoutButton) as Button
        val logoutButton = dialog.findViewById(R.id.confirmLogoutButton) as Button
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        logoutButton.setOnClickListener {
            dialog.dismiss()
            updateStateAndLogOut()
        }
        dialog.show()

    }

    private fun updateStateAndLogOut() {
        setLoggedOut(requireContext(), true)
        setAccessToken(requireContext(), "")
        setRefreshToken(requireContext(), "")
        startActivity(Intent(requireContext(), RegisterActivity::class.java))
        requireActivity().finish()
    }

    private fun navigateToSettingsActivity() {
        userName = getUserName(requireContext()).toString()
        userImageUrl = getUserImageUrl(requireContext()).toString()

        val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
        settingsIntent.putExtra("userFirstName", userName.substringBefore(" "))
        settingsIntent.putExtra("userLastName", userName.substringAfter(" "))
        settingsIntent.putExtra("userImageUrl", userImageUrl)
        startActivityForResult(settingsIntent, SETTINGS_ACTIVITY_CODE)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val isUserNameUpdated = it.getBooleanExtra("userNameUpdated", false)
                    val isUserImageUpdated = it.getBooleanExtra("userImageUpdated", false)
                    val isModeUpdated = it.getBooleanExtra("isModeUpdated", false)

                    if (isModeUpdated) {
                        Log.i(TAG, "MMMMM onActivity: UPDATED")
                        updateMode()
                        requireActivity().recreate()
                    }

                    if (isUserNameUpdated || isUserImageUpdated) {
                        dismissProgressAfterTimeOut()
                        //not the optimal, i should divide updateUserUi into two function, one for the name, and the
                        //other for the image.
                        getUserAndUpdateUiOnLine()
                    }
                }
            }
        }
    }

    private fun updateMode() {
        val appSettingPrefs: SharedPreferences =
            requireActivity().getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUserAndUpdateUiOnLine() {
        bindingInstance.progressBar.visibility = View.VISIBLE
        val userFromViewModelScopeLiveData =
            navDrawerViewModel.getUserWithViewModelScope(accessToken)
        userFromViewModelScopeLiveData.observe(viewLifecycleOwner) {
            it?.let { currentUser ->
                bindingInstance.userNameTextView.text =
                    "${currentUser.firstName} ${currentUser.lastName}"
                bindingInstance.userEmailTextView.text = currentUser.email
                val userImageUrl = "${BASE_USER_IMAGE_URL}${currentUser.image}"

                if (userImageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(userImageUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                bindingInstance.userImageView.setImageBitmap(resource)
                                bindingInstance.progressBar.visibility = View.GONE
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                bindingInstance.progressBar.visibility = View.GONE
                            }
                        })
                } else {
                    bindingInstance.userImageView.setImageResource(R.drawable.avatar)
                }

            }
            if (it == null) {
                bindingInstance.progressBar.visibility = View.GONE
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getUserAndUpdateUiOffLine() {
        bindingInstance.progressBar.visibility = View.VISIBLE

        userName = getUserName(requireContext()).toString()
        userEmail = getEmailFromPrefs(requireContext()).toString()
        userImageUrl = getUserImageUrl(requireContext()).toString()

        bindingInstance.userNameTextView.text = userName
        bindingInstance.userEmailTextView.text = userEmail
        val userImageUrl = "${BASE_USER_IMAGE_URL}${userImageUrl}"

        if (userImageUrl.isNotEmpty()) {
            Glide.with(requireContext())
                .asBitmap()
                .load(userImageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bindingInstance.userImageView.setImageBitmap(resource)
                        bindingInstance.progressBar.visibility = View.GONE
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        bindingInstance.progressBar.visibility = View.GONE
                    }
                })
        } else {
            bindingInstance.userImageView.setImageResource(R.drawable.avatar)
        }


    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            loadingHandler.postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingHandler.removeCallbacksAndMessages(null)
    }

}