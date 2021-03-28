package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.comments.ProductComment
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

private const val TAG = "CommentsRepository"
class CommentsRepository(private val api: Api, private val context: Context) : BaseRepository(context) {

    suspend fun addCommentOnProduct(productComment: ProductComment, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.addCommentOnProduct(productComment, accessToken)} },
                errorMessage = "Adding comment is not available now.")
        }
        catch (ex: Throwable){
            if (ex is HttpException) { exceptionHandler.handleException(ex, ex.code().toString()) }
            else{ exceptionHandler.handleException(ex) }
        }
        return responseMessage
    }

    suspend fun updateCommentOnProduct(commentId: String, productComment: ProductComment, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null

        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.updateCommentOnProduct(commentId, productComment, accessToken)} },
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