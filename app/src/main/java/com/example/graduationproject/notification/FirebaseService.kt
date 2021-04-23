package com.example.graduationproject.notification
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.graduationproject.helper.BaseApplication
import com.example.graduationproject.models.notification.Notifier
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val CHANNEL_ID = "my_channel"
private const val TAG = "Service"
class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.i(TAG, "4444 onNewToken: $newToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val movieId = message.data["movieId"]
        val movieName = message.data["movieName"]
        val notifierName = message.data["notifierName"]
        val notifierId = message.data["notifierId"]
        val notifiedId = message.data["notifiedId"]
        val notifierImageUrl = message.data["notifierImageUrl"]

        Log.i(TAG, "333333 onMessageReceived: $message")
        Log.i(TAG, "333333 onMessageReceived: ${message.data}")
        Log.i(TAG, "333333 onMessageReceived: $movieId")
        Log.i(TAG, "333333 onMessageReceived: $movieName")
        Log.i(TAG, "333333 onMessageReceived: $notifierName")
        Log.i(TAG, "333333 onMessageReceived: $notifierId")
        Log.i(TAG, "333333 onMessageReceived: $notifierImageUrl")


        createClientSideNotification(
            notifierName,
            notifierId,
            notifierImageUrl,
            notifiedId = notifiedId
        )
    }

    private fun createClientSideNotification(
        notifierName: String?,
        notifierId: String?,
        notifierImageUrl: String?,
        notifiedId: String?
    ){

        CoroutineScope(Dispatchers.IO).launch {
            var bitmap: Bitmap? = null
            Glide.with(this@FirebaseService)
                .asBitmap()
                .load(notifierImageUrl)
                .circleCrop()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmap = resource
                        val notifier = Notifier(
                            id = notifierId,
                            name = notifierName,
                            imageUrl = notifierImageUrl,
                            imageBitmap = bitmap
                        )
                        BaseApplication.fireClientSideNotification(notifier)
                    }
                })
        }
    }
}











