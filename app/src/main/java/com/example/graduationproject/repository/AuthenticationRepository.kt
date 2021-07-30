package com.example.graduationproject.repository

import android.content.Context
import android.content.Intent
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.authentication.*
import com.example.graduationproject.network.Api
import com.example.graduationproject.ui.activities.RegisterActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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
                    302 -> exceptionHandler.handleException(ex, "Account already exists")
                    500 -> exceptionHandler.handleException(ex, "${ex.code()}\n${ex.localizedMessage}")
                    405 -> exceptionHandler.handleException(ex, "${ex.code()}\n${ex.localizedMessage}")

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
                        "Internal server error"
                    )
                    404 -> exceptionHandler.handleException(
                        ex,
                        "No account matches this email"
                    )
                    403 -> {
                        exceptionHandler.handleException(ex, "Unverified account")
                        navigateToVerificationFragment()
                    }
                    405 -> {
                        exceptionHandler.handleException(ex, "${ex.code()} ${ex.localizedMessage}")
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

    private fun navigateToVerificationFragment() {
        val intent = Intent(context, RegisterActivity::class.java)
        intent.putExtra("unVerified", 403)
        context.startActivity(intent)
    }

}