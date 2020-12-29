package com.example.graduationproject.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
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
class CommentsRepository(private val api: Api, private val context: Context) : BaseRepository() {

    suspend fun addCommentOnPlace(placeComment: PlaceComment, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null

        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.addCommentOnPlace(placeComment, accessToken)} },
                errorMessage = "Adding comment is not available now.")
        }
        catch (ex: HttpException){
            when(ex.code()){
                404 -> Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
                403 -> {
                    Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
                }
                406 -> Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
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

        return responseMessage
    }

    suspend fun updateCommentOnPlace(placeId: String, placeComment: PlaceComment, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null

        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.updateCommentOnPlace(placeId, placeComment, accessToken)} },
                errorMessage = "Updating comment is not available now.")
        }
        catch (ex: HttpException){
            when(ex.code()){
                404 -> Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
                403 -> {
                    Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
                }
                405 -> {
                    Toast.makeText(context, ex.message(), Toast.LENGTH_SHORT).show()
                }
                406 -> Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
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

        return responseMessage
    }

    suspend fun deleteCommentOnPlace(placeId: String, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null

        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.deleteCommentOnPlace(placeId, accessToken)} },
                errorMessage = "Deleting comment is not available now.")
        }
        catch (ex: HttpException){
            when(ex.code()){
                404 -> Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
                403 -> {
                    Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
                }
                405 -> {
                    Toast.makeText(context, ex.message(), Toast.LENGTH_SHORT).show()
                }
                406 -> Toast.makeText(context, "${ex.code()}", Toast.LENGTH_SHORT).show()
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

        return responseMessage
    }
}