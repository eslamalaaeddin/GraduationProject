package com.example.graduationproject.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.databinding.NavigationDrawerBottomSheetBinding
import com.example.graduationproject.databinding.SearchPlacesBottomSheetBinding
import com.example.graduationproject.model.user.User
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.NavigationDrawerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "NavigationDrawerBottomS"
private const val BASE_USER_IMAGE_URL = "http://10.0.3.2:3000/images/users/"
class NavigationDrawerBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bindingInstance : NavigationDrawerBottomSheetBinding
    private val navDrawerViewModel by viewModel<NavigationDrawerViewModel>()
    private lateinit var accessToken: String
    private var user: User? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(inflater, R.layout.navigation_drawer_bottom_sheet, container, false)
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingInstance.settingsLayout.setOnClickListener { openSettingsBottomSheet() }
    }

    private fun openSettingsBottomSheet() {
        user?.let {
            val settingsBottomSheet = SettingsBottomSheet(it)
            settingsBottomSheet.show(activity?.supportFragmentManager!!, settingsBottomSheet.tag)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        accessToken = SplashActivity.getAccessToken(requireContext()).toString()
        getUser()
    }
    
    @SuppressLint("SetTextI18n")
    private fun getUser(){
        lifecycleScope.launch { 
            val currentUser = navDrawerViewModel.getUser(accessToken)
            currentUser?.let { user ->
                this@NavigationDrawerBottomSheet.user = user
                Log.i(TAG, "TTTT getUser: $user")
                bindingInstance.userNameTextView.text = "${user.firstName} ${user.lastName}"
                bindingInstance.userEmailTextView.text = user.email
                val userImageUrl = "${BASE_USER_IMAGE_URL}${user.image}"
                if (userImageUrl.isNotEmpty()){
                    Picasso.get().load(userImageUrl).into(bindingInstance.userImageView)
                }
                else{
                    bindingInstance.userImageView.setImageResource(R.drawable.cat)
                }
            }
        }
    }


}