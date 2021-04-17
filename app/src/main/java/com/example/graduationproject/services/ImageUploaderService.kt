package com.example.graduationproject.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.graduationproject.R
import com.example.graduationproject.helpers.Constants.ACTION_IMAGE_UPLOADED_FAIL
import com.example.graduationproject.helpers.Constants.ACTION_IMAGE_UPLOADED_SUCCESS
import com.example.graduationproject.helpers.Constants.ACTION_IMAGE_UPLOADED_SUCCESS_NO_UI
import com.example.graduationproject.helpers.Constants.CHANNEL_ID
import com.example.graduationproject.helpers.Constants.CHANNEL_NAME
import com.example.graduationproject.helpers.Constants.NOTIFICATION_ID
import com.example.graduationproject.helpers.Constants.TIME_OUT_MILLISECONDS
import com.example.graduationproject.helpers.fileutils.FileUtils
import com.example.graduationproject.models.ImageResponse
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.ui.activities.SplashActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "ImageUploaderService"

class ImageUploaderService : Service() {
    private lateinit var notification: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManagerCompat
    val handler = Handler()
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notification = NotificationCompat.Builder(this, CHANNEL_NAME)
        notificationManager = NotificationManagerCompat.from(this)
        showImageUploadingProgress()
        if (intent != null) {
            val imageUrl = intent.data
            val accessToken = intent.getStringExtra("accessToken").toString()
            val oldImageName = intent.getStringExtra("oldImageName").toString()
            if (imageUrl != null) {
                uploadImage(imageUrl, accessToken, oldImageName)
            }
        }

        return START_REDELIVER_INTENT

    }

    private fun uploadImage(imageUri: Uri, accessToken: String, oldImageName: String) {
        val descriptionPart = RequestBody.create(MultipartBody.FORM, "image")
        val theRequiredFile = FileUtils.getFile(this, imageUri)
        Log.i(TAG, "SSS uploadImage: ${theRequiredFile.length()}")
        val filePart = theRequiredFile.let {
            RequestBody.create(
                contentResolver.getType(imageUri)?.toMediaTypeOrNull(),
                it
            )
        }
        val file = filePart.let {
            MultipartBody.Part.createFormData(
                "image",
                theRequiredFile.name,
                it
            )
        }
        val retrofit = RetrofitInstance.api
        val call = retrofit.uploadImage(
            image = file,
            accessToken = accessToken,
            description = descriptionPart,
            oldImageName = oldImageName
        )
        call?.enqueue(object : Callback<ImageResponse?> {

            override fun onResponse(call: Call<ImageResponse?>, response: Response<ImageResponse?>) {
                if (response.isSuccessful) {
                    stopForeground(true)
                    notification.setContentText("Done :)")
                        .setProgress(0, 0, false)
                        .setOngoing(false)
                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                    handler.removeCallbacksAndMessages(null)
                    //send a broad cast to update the ui
                    val intent = Intent(ACTION_IMAGE_UPLOADED_SUCCESS)
                    val noUiIntent = Intent(ACTION_IMAGE_UPLOADED_SUCCESS_NO_UI)
                    sendBroadcast(intent)
                    sendBroadcast(noUiIntent)
                    response.body()?.let {
                        SplashActivity.setUserImageUrl(this@ImageUploaderService, it.image.orEmpty())
                    }
                }
            }
            //1618500110-56681229741093.jpg

            override fun onFailure(call: Call<ImageResponse?>, t: Throwable) {
                stopForeground(true)
                notification.setContentText("Failed :(")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManager.notify(NOTIFICATION_ID, notification.build())
                handler.removeCallbacksAndMessages(null)
                val intent = Intent(ACTION_IMAGE_UPLOADED_FAIL)
                sendBroadcast(intent)
                Log.i(TAG, "TTTT onResponse: ${t.localizedMessage}")
            }

        })

    }

    private fun showImageUploadingProgress() {
        val progressMax = 100

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(null, null)
            channel.setShowBadge(false)
            channel.importance = NotificationManager.IMPORTANCE_HIGH
            channel.description = getString(R.string.channel_description)
            notificationManager.createNotificationChannel(channel)
        }

        notification.setSmallIcon(R.drawable.recommended)
            .setChannelId(CHANNEL_ID)
            .setContentTitle("Image uploading")
            .setContentText("Uploading in progress")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(progressMax, 0, true)

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(
            NOTIFICATION_ID,
            notification.build()
        )
        startForeground(NOTIFICATION_ID, notification.build())

        handler.postDelayed({
            stopForeground(true)
            notification.setContentText("Failed :(")
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(NOTIFICATION_ID, notification.build())

        }, TIME_OUT_MILLISECONDS)
    }

}