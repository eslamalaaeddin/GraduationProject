package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.model.products.Product
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

class SearchRepository(
    private val context: Context,
    private val ioDispatcher : CoroutineContext = Dispatchers.IO,
    private val api: Api
    ) : BaseRepository(context) {

    suspend fun searchByProductTag(tag : String, page: Int, accessToken: String): List<Product>? {
        var response : List<Product>? = null
        try {
            response = safeApiCall(
                call = { withContext(ioDispatcher){api.searchByProductTag(tag, page, accessToken)} },
                errorMessage = "Error Fetching Recommended Places")
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }

        return response
    }

    suspend fun searchByProductName(productName : String, page: Int, accessToken: String): List<Product>? {
        var response : List<Product>? = null
        try {
            response = safeApiCall(
                call = { withContext(ioDispatcher){api.searchByProductName(productName, page, accessToken)} },
                errorMessage = "Error Fetching Recommended Places")
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }

        return response
    }

}