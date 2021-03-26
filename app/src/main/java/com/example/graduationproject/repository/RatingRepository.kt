package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RatingRepository(private val api: Api, private val context: Context): BaseRepository(context) {

    suspend fun addRatingToProduct(rate: Rate, placeId: String, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(Dispatchers.IO){api.addRatingToPlace(placeId, rate, accessToken)} },
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

    suspend fun getProductRate(placeId: String, accessToken: String): Rate? {
        var rate: Rate? = null
        try {
            rate = safeApiCall(
                call = { withContext(Dispatchers.IO){api.getProductRate(placeId, accessToken)} },
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