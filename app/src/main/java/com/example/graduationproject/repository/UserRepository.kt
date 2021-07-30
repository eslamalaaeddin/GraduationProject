package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.user.User
import com.example.graduationproject.models.user.UserName
import com.example.graduationproject.models.user.UserPassword
import com.example.graduationproject.network.Api
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UserRepository(private val api: Api,
                     private val context: Context,
                     private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): BaseRepository(context) {

    suspend fun getUser(accessToken: String): User?{
        var user: User? = null

        try {
            user = safeApiCall(
                call = { withContext(ioDispatcher){api.getUser(accessToken)}},
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
                call = { withContext(ioDispatcher){api.updateUserName(userName, accessToken)}},
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
                call = { withContext(ioDispatcher){api.changeUserPassword(userPassword, accessToken)}},
                errorMessage = "Error changing user password"
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){
                if (ex.code() == 406)
                    exceptionHandler.handleException(ex, "Wrong password")
            }
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }
}