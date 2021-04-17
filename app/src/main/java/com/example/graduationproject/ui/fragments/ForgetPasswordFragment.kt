package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.authentication.ResetCode
import com.example.graduationproject.network.RetrofitInstance
import kotlinx.android.synthetic.main.fragment_password_forget.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordFragment : Fragment(R.layout.fragment_password_forget ) {

    private val api = RetrofitInstance.api
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendPasswordLinkButton.setOnClickListener {
            val email = email_address_edit_text.text.toString()
            validateEmailAndNavigateToResetPassword(email)

        }
    }

    private fun validateEmailAndNavigateToResetPassword(email: String){
        if (email.isEmpty()){
            Toast.makeText(requireContext(), "Please enter email first", Toast.LENGTH_SHORT).show()
        }
        else{
            val resetCode = ResetCode(email)
            val call = api.sendResetCode(resetCode)
            call.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    Toast.makeText(requireContext(), "${response.code()}", Toast.LENGTH_SHORT).show()
                    val responseCode = response.code()
                    if (responseCode == 404) {
                        Toast.makeText(
                            requireContext(),
                            "No account matches this email",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (responseCode == 200) {
                        Toast.makeText(
                            context,
                            "Code has been sent to your email, please check it.",
                            Toast.LENGTH_LONG
                        ).show()
                        val action =
                            ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToResetPasswordFragment()
                        findNavController().navigate(action)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }
}