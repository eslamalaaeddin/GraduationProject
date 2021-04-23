package com.example.graduationproject.ui.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.graduationproject.R
import com.example.graduationproject.databinding.SettingBottomSheetBinding
import com.example.graduationproject.helper.Constants.BASE_USER_IMAGE_URL
import com.example.graduationproject.models.user.User
import com.example.graduationproject.models.user.UserName
import com.example.graduationproject.models.user.UserPassword
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodels.NavigationDrawerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsBottomSheet(private val user: User): BottomSheetDialogFragment() {
    private lateinit var bindingInstance: SettingBottomSheetBinding
    private lateinit var accessToken: String
    private val navDrawerViewModel by viewModel<NavigationDrawerViewModel>()
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
        bindingInstance = DataBindingUtil.inflate(inflater, R.layout.setting_bottom_sheet, container, false)
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = SplashActivity.getAccessToken(requireContext()).toString()
       // updateUserUi()

        bindingInstance.upButtonView.setOnClickListener {dismiss()}
        bindingInstance.changePasswordImageButton.setOnClickListener {
           showUpdateUserPasswordDialog()
        }

        bindingInstance.updateInfoButton.setOnClickListener {
            val firstName = bindingInstance.firstNameEditText.text.toString()
            val lastName = bindingInstance.lastNameEditText.text.toString()
            if (firstName.isEmpty() || lastName.isEmpty()){
                Toast.makeText(requireContext(), "Enter all information first.", Toast.LENGTH_SHORT).show()
            }
            else{
                updateUserInfo(firstName, lastName)
            }
        }
    }

    private fun showUpdateUserPasswordDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_password_dialog)

        val oldPasswordEditText = dialog.findViewById(R.id.oldPasswordEditText) as EditText
        val newPasswordEditText = dialog.findViewById(R.id.newPasswordEditText) as EditText

        val cancelButton = dialog.findViewById(R.id.cancelChangePasswordButton) as Button
        val submitButton = dialog.findViewById(R.id.SubmitChangePasswordButton) as Button
        submitButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            if (oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()){
                Toast.makeText(requireContext(), "Enter all information first.", Toast.LENGTH_SHORT).show()
            }
            else{
                val userPassword = UserPassword(oldPassword, newPassword)
                lifecycleScope.launch {
                    bindingInstance.progressBar?.visibility = View.VISIBLE
                    submitButton.isEnabled = false
                    val responseMessage = navDrawerViewModel.changeUserPassword(userPassword, accessToken)
                    responseMessage?.let {
                        bindingInstance.progressBar?.visibility = View.GONE
                        submitButton.isEnabled = true
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    if (responseMessage == null){
                        bindingInstance.progressBar?.visibility = View.GONE
                        submitButton.isEnabled = true
                    }
                }
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun changePassword(oldPassword: String, newPassword: String){
        if (oldPassword.isEmpty() || newPassword.isEmpty()){
            Toast.makeText(requireContext(), "Enter all information first.", Toast.LENGTH_SHORT).show()
        }
        else{
            val userPassword = UserPassword(oldPassword, newPassword)
            lifecycleScope.launch {
                val responseMessage = navDrawerViewModel.changeUserPassword(userPassword, accessToken)
                responseMessage?.let {
                    Toast.makeText(requireContext(), "Password changed.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserInfo(firstName: String, lastName: String) {
        //check to not make a dummy request with the same data
//        if (firstName != user.firstName && lastName != user.lastName) {
            val userName = UserName(firstName, lastName)
            lifecycleScope.launch {
                bindingInstance.updateInfoButton.isEnabled = false
                bindingInstance.progressBar?.visibility = View.VISIBLE
                val responseMessage = navDrawerViewModel.updateUserName(userName, accessToken)
                responseMessage?.let {
                    bindingInstance.updateInfoButton.isEnabled = true
                    bindingInstance.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), "Updated successfully.", Toast.LENGTH_SHORT)
                        .show()
                }
                if (responseMessage == null){
                    bindingInstance.progressBar?.visibility = View.GONE
                    bindingInstance.updateInfoButton.isEnabled = true
                }
            }
//        }
    }

    private fun updateUserUi(){

        val userImageUrl = "$BASE_USER_IMAGE_URL${user.image}"
        if (userImageUrl.isNotEmpty()){
            Glide.with(requireContext())
                .load(userImageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(bindingInstance.userImageView)
        }
        bindingInstance.firstNameEditText.setText(user.firstName)
        bindingInstance.firstNameEditText.setSelection(bindingInstance.firstNameEditText.text.length)

        bindingInstance.lastNameEditText.setText(user.lastName)
        bindingInstance.lastNameEditText.setSelection(bindingInstance.lastNameEditText.text.length)

    }



}