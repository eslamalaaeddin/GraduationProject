package com.example.graduationproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentPasswordResetBinding

class ResetPasswordFragment : Fragment() {
    private lateinit var resetBinding: FragmentPasswordResetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        resetBinding =
            DataBindingUtil.inflate(inflater , R.layout.fragment_password_reset , container,false)

        return resetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetBinding.saveNewPasswordButton.setOnClickListener {
            val signInFragment = SignInFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, signInFragment)
                ?.commit()

            Toast.makeText(context, "New password is saved successfully", Toast.LENGTH_SHORT).show()
        }
    }
}