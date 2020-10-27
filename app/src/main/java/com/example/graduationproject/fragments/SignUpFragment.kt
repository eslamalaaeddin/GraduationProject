package com.example.graduationproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentUpSignBinding

class SignUpFragment : Fragment() {
    private lateinit var signUpBinding: FragmentUpSignBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signUpBinding =
            DataBindingUtil.inflate(inflater , R.layout.fragment_up_sign , container,false)

        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpBinding.haveAccountTextView.setOnClickListener {

            val signInFragment = SignInFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, signInFragment)
                ?.commit()

        }

        signUpBinding.createAccountButton.setOnClickListener {
            Toast.makeText(context, "Verification code has been sent, please check your phone.", Toast.LENGTH_LONG).show()
            val verificationFragment = VerificationFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, verificationFragment)
                ?.commit()

        }
    }
}