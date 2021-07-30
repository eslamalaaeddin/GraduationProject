package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.rating.Rate
import com.example.graduationproject.network.Api
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RatingRepository(private val api: Api, private val context: Context, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): BaseRepository(context) {

    suspend fun addRatingToProduct(rate: Rate, productId: String, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(ioDispatcher){api.addRatingToProduct(productId, rate, accessToken)} },
                errorMessage = "Rating product is not available now."
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun updateRatingToProduct(rate: Rate, productId: String, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall(
                call = { withContext(ioDispatcher){api.updateRatingToProduct(productId, rate, accessToken)} },
                errorMessage = "Update Rating product is not available now."
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun getUserSpecificRate(productId: String, accessToken: String): Rate? {
        var rate: Rate? = null
        try {
            rate = safeApiCall(
                call = { withContext(ioDispatcher){api.getUserSpecificRate(productId, accessToken)} },
                errorMessage = "Getting your rate on product is not available now."
            )
        }
        catch (ex: Throwable){
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return rate
    }
}