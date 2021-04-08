package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.network.Api
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RatingRepository(private val api: Api, private val context: Context, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): BaseRepository(context) {

    suspend fun addRatingToProduct(rate: Rate, placeId: String, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(ioDispatcher){api.addRatingToPlace(placeId, rate, accessToken)} },
                errorMessage = "Rating place is not available now."
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun updateRatingToProduct(rate: Rate, placeId: String, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(ioDispatcher){api.updateRatingToPlace(placeId, rate, accessToken)} },
                errorMessage = "Update Rating place is not available now."
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun getUserSpecificRate(placeId: String, accessToken: String): Rate? {
        var rate: Rate? = null
        try {
            rate = safeApiCall(
                call = { withContext(ioDispatcher){api.getUserSpecificRate(placeId, accessToken)} },
                errorMessage = "Getting your rate on place is not available now."
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return rate
    }
}