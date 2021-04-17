package com.example.graduationproject.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.graduationproject.R
import com.example.graduationproject.databinding.UserInfoFragmentBinding
import com.example.graduationproject.dummy.PostAttachmentListener
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.helpers.Constants.BASE_USER_IMAGE_URL
import com.example.graduationproject.helpers.Constants.SETTINGS_ACTIVITY_CODE
import com.example.graduationproject.models.user.User
import com.example.graduationproject.ui.activities.RegisterActivity
import com.example.graduationproject.ui.activities.SettingsActivity
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodels.NavigationDrawerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "NavigationDrawerBottomS"


class UserInfoFragment : Fragment(), PostAttachmentListener{
    private lateinit var bindingInstance: UserInfoFragmentBinding
    private val navDrawerViewModel by viewModel<NavigationDrawerViewModel>()
    private lateinit var accessToken: String
    private var user: User? = null
    private var userName: String = ""
    private var userImageUrl: String = ""
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

        dismissProgressAfterTimeOut()
        getUserAndUpdateUi()

//        bindingInstance.userImageView.setOnClickListener{
//            val addToPostBottomSheet = AddToPostBottomSheet(this)
//            activity?.supportFragmentManager?.let { it1 -> addToPostBottomSheet.show(it1, addToPostBottomSheet.tag) }
//        }
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
        userName =  SplashActivity.getUserName(requireContext()).toString()
        userImageUrl =  SplashActivity.getUserImageUrl(requireContext()).toString()

        val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
        settingsIntent.putExtra("userFirstName", userName.substringBefore(" "))
        settingsIntent.putExtra("userLastName",  userName.substringAfter(" "))
        settingsIntent.putExtra("userImageUrl", userImageUrl)
        startActivityForResult(settingsIntent, SETTINGS_ACTIVITY_CODE)

//        user?.let {
//            val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
//
//
//            //get user data from prefernces
//            settingsIntent.putExtra("userFirstName", it.firstName)
//            settingsIntent.putExtra("userLastName", it.lastName)
//            settingsIntent.putExtra("userImageUrl", it.image)
////            startActivity(settingsIntent)
//            startActivityForResult(settingsIntent, SETTINGS_ACTIVITY_CODE)
//        }
//        if (user == null){
//            Toast.makeText(requireContext(), "Please check your connectivity or try reloading the page", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_ACTIVITY_CODE){
            if (resultCode == Activity.RESULT_OK){
                data?.let {
                    val isUserNameUpdated = it.getBooleanExtra("userNameUpdated", false)
                    val isUserImageUpdated = it.getBooleanExtra("userImageUpdated", false)

                    if (isUserNameUpdated || isUserImageUpdated){
                        dismissProgressAfterTimeOut()
                        //not the optimal, i should divide updateUserUi into two function, one for the name, and the
                        //other for the image.
                        getUserAndUpdateUi()
                    }
                }
            }
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
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(object : CustomTarget<Bitmap>(){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                bindingInstance.userImageView.setImageBitmap(resource)
                                bindingInstance.progressBar.visibility = View.GONE
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {
                                bindingInstance.progressBar.visibility = View.GONE
                            }
                        })
//                    Picasso.get().load(userImageUrl).into(bindingInstance.userImageView)

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

    override fun onAttachmentAdded(data: Intent?, dataType: String, fromCamera: Boolean) {
        Log.i(TAG, "555555  $data")
        Log.i(TAG, "555555  ${data?.data}")
        Log.i(TAG, "555555  ${data?.data?.encodedPath}")
        Log.i(TAG, "555555  ${data?.toUri(0)}")
    }


}