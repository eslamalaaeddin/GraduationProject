package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RatingRepository(private val api: Api, private val context: Context): BaseRepository(context) {

    suspend fun addRatingToPlace(rate: Rate, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.addRatingToPlace(rate, accessToken)} },
                errorMessage = "Rating place is not available now."
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun updateRatingToPlace(placeId: String, rate: Rate, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.updateRatingToPlace(placeId, rate, accessToken)} },
                errorMessage = "Update Rating place is not available now."
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }
}