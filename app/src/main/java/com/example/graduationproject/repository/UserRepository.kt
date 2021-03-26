package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.user.User
import com.example.graduationproject.model.user.UserName
import com.example.graduationproject.model.user.UserPassword
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UserRepository(private val api: Api, private val context: Context): BaseRepository(context) {

    suspend fun getUser(accessToken: String): User?{
        var user: User? = null
        try {
            user = safeApiCall(
                call = { withContext(Dispatchers.IO){api.getUser(accessToken)}},
                errorMessage = "Error getting user info"
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }

        return user
    }
    suspend fun updateUserName(userName: UserName, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.updateUserName(userName, accessToken)}},
                errorMessage = "Error changing user name"
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun changeUserPassword(userPassword: UserPassword, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.changeUserPassword(userPassword, accessToken)}},
                errorMessage = "Error changing user password"
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }
}