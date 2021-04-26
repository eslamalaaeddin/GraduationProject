package com.example.graduationproject.notification

import android.util.Log
import com.example.graduationproject.models.notification.Notification
import kotlinx.coroutines.*

private const val TAG = "NotificationsHandler"

class NotificationsHandler(
    var notifierId: String? = null,
    var notifierName: String? = null,
    var notifierImageUrl: String? = null,
    var notifiedId: String? = null,
    var movieId: Long? = 0,
    var movieName: String? = null,
    var notifiedTokens: MutableList<String> = mutableListOf()
) {


    //[1]
    private fun createNotification(): Notification {
        return Notification(
            notifierId = notifierId,
            notifiedId = notifiedId,
            notifierName = notifierName,
            notifierImageUrl = notifierImageUrl,
            movieId = movieId,
            movieName = movieName
        )
    }

    fun fireServerSideNotification() = CoroutineScope(
        Dispatchers.IO
    ).launch {
        val notification = createNotification()
        try {
            notifiedTokens.forEach {

                val pushNotification = PushNotification(
                    data = notification,
                    to = it
                )
                RetrofitInstance.api.postNotification(pushNotification)
            }
//            val pushNotification = PushNotification(
//                data = notification,
//                to = notifiedToken
//            )
//            val response = RetrofitInstance.api.postNotification(pushNotification)
//            if (response.isSuccessful) {
//                Log.i(TAG, "333333: $response")
//                Log.i(TAG, "333333:BODY ${response.body()}")
//                Log.i(TAG, "333333:MESSAGE ${response.message()}")
//                Log.i(TAG, "333333:RAW ${response.raw()}")
//                Log.i(TAG, "333333:HEADERS ${response.headers()}")
//                Log.i(TAG, "333333 Response: Success")
//            } else {
//                Log.e(TAG, response.errorBody().toString())
//            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }


}