package com.example.graduationproject.helper

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ExceptionHandler(private val context: Context) {

    fun handleException(ex: Throwable, message: String? = null){
        when (ex) {
            is HttpException -> {
//                showErrorMessage(message)
                showErrorMessage(ex.localizedMessage)
            }
            is IOException -> {
//                showErrorMessage("Please check your internet connection")
                showErrorMessage(ex.localizedMessage)
            }
            else -> {
//                showErrorMessage ("Something went wrong")
                showErrorMessage(ex.localizedMessage)
            }
        }
    }

    private fun showErrorMessage(message: String?) {
        CoroutineScope(Dispatchers.Main).launch{
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}