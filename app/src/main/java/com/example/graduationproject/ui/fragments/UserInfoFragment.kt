package com.example.graduationproject.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.graduationproject.R
import com.example.graduationproject.databinding.UserInfoFragmentBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Constants.BASE_USER_IMAGE_URL
import com.example.graduationproject.helper.Constants.CHANNEL_ID
import com.example.graduationproject.helper.Constants.CHANNEL_NAME
import com.example.graduationproject.helper.FileUtils
import com.example.graduationproject.model.user.User
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.ui.activities.RegisterActivity
import com.example.graduationproject.ui.activities.SettingsActivity
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.NavigationDrawerViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private const val TAG = "NavigationDrawerBottomS"


class UserInfoFragment : Fragment(){
    private lateinit var bindingInstance: UserInfoFragmentBinding
    private val navDrawerViewModel by viewModel<NavigationDrawerViewModel>()
    private lateinit var accessToken: String
    private var user: User? = null

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
        accessToken = SplashActivity.getAccessToken(requireContext()).toString()
        bindingInstance.settingsLayout.setOnClickListener { navigateToSettingsActivity() }
        bindingInstance.logOutLayout.setOnClickListener {
            showLogoutDialog()
        }

    }

    override fun onStart() {
        super.onStart()
        dismissProgressAfterTimeOut()
        getUserAndUpdateUi()
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
            updateStateAndLogOut()
        }
        dialog.show()

    }

    private fun updateStateAndLogOut() {
        SplashActivity.setLoggedOut(requireContext(), true)
        SplashActivity.setAccessToken(requireContext(), "")
        SplashActivity.setRefreshToken(requireContext(), "")
        startActivity(Intent(requireContext(), RegisterActivity::class.java))
        requireActivity().finish()
    }

    private fun navigateToSettingsActivity() {
        user?.let {
            val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
            settingsIntent.putExtra("userFirstName", it.firstName)
            settingsIntent.putExtra("userLastName", it.lastName)
            settingsIntent.putExtra("userImageUrl", it.image)

            startActivity(settingsIntent)
        }
        if (user == null){
            Toast.makeText(requireContext(), "Please check your connectivity", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUserAndUpdateUi(){
        bindingInstance.progressBar.visibility = View.VISIBLE
        val userFromViewModelScopeLiveData =
            navDrawerViewModel.getUserWithViewModelScope(accessToken)
        userFromViewModelScopeLiveData.observe(viewLifecycleOwner) {
            it?.let { currentUser ->
                user = currentUser
                bindingInstance.userNameTextView.text = "${currentUser.firstName} ${currentUser.lastName}"
                bindingInstance.userEmailTextView.text = currentUser.email
                val userImageUrl = "${BASE_USER_IMAGE_URL}${currentUser.image}"

                if (userImageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(userImageUrl)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(object : CustomTarget<Bitmap>(){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                bindingInstance.userImageView.setImageBitmap(resource)
                                bindingInstance.progressBar.visibility = View.GONE
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {
                                bindingInstance.progressBar.visibility = View.GONE
                                // this is called when imageView is cleared on lifecycle call or for
                                // some other reason.
                                // if you are referencing the bitmap somewhere else too other than this imageView
                                // clear it here as you can no longer have the bitmap
                            }
                        })

                } else {
                    bindingInstance.userImageView.setImageResource(R.drawable.avatar)
                }
                //bindingInstance.progressBar.visibility = View.GONE

            }
            if (it == null){
                bindingInstance.progressBar.visibility = View.GONE
            }
        }

    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            Handler().postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }


}