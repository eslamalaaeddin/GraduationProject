package com.example.graduationproject.repository

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.graduationproject.R
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.*
import com.example.graduationproject.network.Api
import com.example.graduationproject.ui.activities.RegisterActivity
import com.example.graduationproject.ui.fragments.SignUpFragmentDirections
import kotlinx.android.synthetic.main.unverified_account_dialog_layout.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

private const val TAG = "AuthRepository"

class AuthenticationRepository(
    private val api: Api,
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(context) {

    suspend fun signUp(signUp: SignUp): ResponseMessage? {
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(ioDispatcher) { api.signUp(signUp) } },
                errorMessage = "Sign up is not available now."
            )
        } catch (ex: Throwable) {
            if (ex is HttpException) {
                when (ex.code()) {
                    302 -> exceptionHandler.handleException(
                        ex,
                        "${ex.code()} Account already exists"
                    )
                    500 -> exceptionHandler.handleException(
                        ex,
                        "${ex.code()} Account created but with internal"
                    )
                }
            } else {
                exceptionHandler.handleException(ex)
            }
        }


        //this is not necessary but, leave it for now
        if (responseMessage != null) {
            responseMessage.responseCode = 201
        }
        return responseMessage
    }

    suspend fun login(login: Login): Token? {
        var token: Token? = null
        try {
            token = safeApiCall(
                call = { withContext(ioDispatcher) { api.login(login) } },
                errorMessage = "Login is not available now."
            )
        } catch (ex: Throwable) {
            if (ex is HttpException) {
                when (ex.code()) {
                    500 -> exceptionHandler.handleException(
                        ex,
                        "${ex.code()} Internal server error"
                    )
                    404 -> exceptionHandler.handleException(
                        ex,
                        "${ex.code()} No account matches this email"
                    )
                    403 -> {
                        exceptionHandler.handleException(ex, "${ex.code()} Unverified account")
                        navigateToVerificationFragment()
                    }
                    406 -> exceptionHandler.handleException(ex, "Incorrect password")
                }
            } else {
                exceptionHandler.handleException(ex)
            }

        }

        return token
    }

    suspend fun verifyUser(verify: Verify): Token? {
        var token: Token? = null
        try {
            token = safeApiCall(
                call = { withContext(ioDispatcher) { api.verifyUser(verify) } },
                errorMessage = "Verification is not available now."
            )
        } catch (ex: Throwable) {
            if (ex is HttpException) {
                exceptionHandler.handleException(ex, "${ex.code()} Incorrect code")
            } else {
                exceptionHandler.handleException(ex)
            }
        }

        return token
    }

    suspend fun refreshToken(refreshToken: RefreshToken): Token? {
        var token: Token? = null
        try {
            token = safeApiCall(
                call = { withContext(ioDispatcher) { api.refreshToken(refreshToken) } },
                errorMessage = "Refreshing token is not available now."
            )
        } catch (ex: Throwable) {
            if (ex is HttpException) {
                exceptionHandler.handleException(
                    ex,
                    "${ex.code()} Token is expired or invalid\nSomething went wrong"
                )
            } else {
                exceptionHandler.handleException(ex)
            }
        }

        return token
    }

    private fun navigateToVerificationFragment() {
        val intent = Intent(context, RegisterActivity::class.java)
        intent.putExtra("unVerified", 403)
        context.startActivity(intent)
    }

}