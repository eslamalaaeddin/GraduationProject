package com.example.graduationproject.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.graduationproject.helper.ExceptionHandler
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.Login
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.model.comments.PlaceComment
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "CommentsRepository"
class CommentsRepository(private val api: Api, private val context: Context) : BaseRepository(context) {

    suspend fun addCommentOnProduct(placeComment: PlaceComment, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.addCommentOnProduct(placeComment, accessToken)} },
                errorMessage = "Adding comment is not available now.")
        }
        catch (ex: Throwable){
            if (ex is HttpException) { exceptionHandler.handleException(ex, ex.code().toString()) }
            else{ exceptionHandler.handleException(ex) }
        }
        return responseMessage
    }

    suspend fun updateCommentOnProduct(commentId: String, placeComment: PlaceComment, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null

        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.updateCommentOnProduct(commentId, placeComment, accessToken)} },
                errorMessage = "Updating comment is not available now.")
        }
        catch (ex: Throwable){
            if (ex is HttpException) { exceptionHandler.handleException(ex, ex.code().toString()) }
            else{ exceptionHandler.handleException(ex)}
        }

        return responseMessage
    }

    suspend fun deleteCommentFromProduct(commentId: String, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null

        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.deleteCommentFromProduct(commentId, accessToken)} },
                errorMessage = "Deleting comment is not available now.")
        }
        catch (ex: Throwable){
            if (ex is HttpException) { exceptionHandler.handleException(ex, ex.code().toString()) }
            else{ exceptionHandler.handleException(ex)}
        }

        return responseMessage
    }
}