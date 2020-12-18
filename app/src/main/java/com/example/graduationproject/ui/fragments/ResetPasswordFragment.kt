package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import kotlinx.android.synthetic.main.fragment_password_reset.*

class ResetPasswordFragment : Fragment(R.layout.fragment_password_reset) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveNewPasswordButton.setOnClickListener {
            Toast.makeText(context, "New password is saved successfully", Toast.LENGTH_SHORT).show()
            val action = ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }
}