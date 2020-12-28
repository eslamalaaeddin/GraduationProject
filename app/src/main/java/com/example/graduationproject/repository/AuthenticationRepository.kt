package com.example.graduationproject.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.*
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "AuthRepository"
class AuthenticationRepository(private val api: Api, private val context: Context): BaseRepository() {

    suspend fun signUp(signUp: SignUp):ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.signUp(signUp)}},
                errorMessage = "Sign up is not available now.")
        }
        catch (ex: HttpException){
            when(ex.code()){
                302 -> Toast.makeText(context, "Account already exists", Toast.LENGTH_SHORT).show()
                500 -> Toast.makeText(context, "Account created but with internal", Toast.LENGTH_SHORT).show()
            }
        }
        catch (ex: IOException){
            Log.i(TAG, "sign up: ${ex.message.toString()}")
            Toast.makeText(context, "${ex.message.toString()} please check your internet connection", Toast.LENGTH_SHORT).show()
        }
        catch (ex: Throwable){
            Log.i(TAG, "sign up: ${ex.message.toString()}")
            Toast.makeText(context, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        //this is not necessary but, leave it for now
        if (responseMessage != null) {
            responseMessage.responseCode = 201
        }
        return responseMessage
    }

    suspend fun login(login: Login): Token?{
        var token: Token? = null
        try {
            token = safeApiCall(
                call = {withContext(Dispatchers.IO){api.login(login)}},
                errorMessage = "Login is not available now.")
        }
        catch (ex: HttpException){
            when(ex.code()){
                404 -> Toast.makeText(context, "No account matches this email", Toast.LENGTH_SHORT).show()
                403 -> Toast.makeText(context, "Unverified account", Toast.LENGTH_SHORT).show()
                406 -> Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }
        catch (ex: IOException){
            Log.i(TAG, "login: ${ex.message.toString()}")
            Toast.makeText(context, "${ex.message.toString()} please check your internet connection", Toast.LENGTH_SHORT).show()
        }
        catch (ex: Throwable){
            Log.i(TAG, "login: ${ex.message.toString()}")
            Toast.makeText(context, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return token
    }

    suspend fun verifyUser(verify: Verify): Token?{
        var token: Token? = null
        try {
            token = safeApiCall(
                call = {withContext(Dispatchers.IO){api.verifyUser(verify)}},
                errorMessage = "Verification is not available now.")
        }
        catch (ex: HttpException){
            when(ex.code()){ 406 -> Toast.makeText(context, "Incorrect code", Toast.LENGTH_SHORT).show() }
        }
        catch (ex: IOException){
            Toast.makeText(context, "${ex.message.toString()} please check your internet connection", Toast.LENGTH_SHORT).show()
        }
        catch (ex: Throwable){
            Toast.makeText(context, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return token
    }

    suspend fun refreshToken(refreshToken: RefreshToken): Token?{
        var token: Token? = null
        try {
            token = safeApiCall(
                call = {withContext(Dispatchers.IO){api.refreshToken(refreshToken)}},
                errorMessage = "Refreshing token is not available now.")
        }
        catch (ex: HttpException){
            when(ex.code()){ 403 -> Toast.makeText(context, "Token is expired or invalid", Toast.LENGTH_SHORT).show() }
        }
        catch (ex: IOException){
            Toast.makeText(context, "${ex.message.toString()} please check your internet connection", Toast.LENGTH_SHORT).show()
        }
        catch (ex: Throwable){
            Toast.makeText(context, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return token
    }

}