package com.example.graduationproject.repository

import android.content.Context
import android.util.Log
import com.example.graduationproject.helper.ExceptionHandler
import com.example.graduationproject.helper.Result
import retrofit2.HttpException
import retrofit2.Response

private const val TAG = "BaseRepository"
open class BaseRepository (context: Context){

    var mContext = context

    val exceptionHandler = ExceptionHandler(context)

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String? = null): T? {

        val result : Result<T> = safeApiResult(call,errorMessage.orEmpty())
        var data : T? = null

        when(result) {
            is Result.Success ->
                data = result.data
            is Result.Error -> {
                Log.i(TAG, result.exception.message.toString())
            }
        }

        return data

    }

    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>, errorMessage: String) : Result<T>{
        val response = call.invoke()
        if(response.isSuccessful) {
            return Result.Success(response.body()!!)
        }
        throw HttpException(response)
    }
}