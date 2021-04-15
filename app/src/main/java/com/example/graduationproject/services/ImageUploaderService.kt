package com.example.graduationproject.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.graduationproject.R
import com.example.graduationproject.helper.Constants.ACTION_IMAGE_UPLOADED
import com.example.graduationproject.helper.Constants.CHANNEL_ID
import com.example.graduationproject.helper.Constants.CHANNEL_NAME
import com.example.graduationproject.helper.Constants.NOTIFICATION_ID
import com.example.graduationproject.helper.FileUtils
import com.example.graduationproject.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


private const val TAG = "ImageUploaderService"

class ImageUploaderService : Service() {
    private lateinit var notification: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notification = NotificationCompat.Builder(this, CHANNEL_NAME)
        notificationManager = NotificationManagerCompat.from(this)
        showImageUploadingProgress()
        if (intent != null){
            val imageUrl = intent.data
            val accessToken = intent.getStringExtra("accessToken").toString()
            if (imageUrl != null){
                uploadImage(imageUrl, accessToken)
            }
        }

        return START_REDELIVER_INTENT

    }

    private fun uploadImage(imageUri: Uri, accessToken: String) {
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
                description = descriptionPart
            )
            call?.enqueue(object : Callback<ResponseBody?> {

                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        stopForeground(true)
                        notification.setContentText("Done.")
                            .setProgress(0, 0, false)
                            .setOngoing(false)
                        notificationManager.notify(NOTIFICATION_ID, notification.build())
                        //send a broad cast to update the ui
                        val intent = Intent(ACTION_IMAGE_UPLOADED)
                        sendBroadcast(intent)
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    stopForeground(true)
                    notification.setContentText("Failed...try again.")
                        .setProgress(0, 0, false)
                        .setOngoing(false)
                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                    Log.i(TAG, "TTTT onResponse: ${t.localizedMessage}")
                }

            })

    }

    private fun showImageUploadingProgress() {
        val progressMax = 100

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setSound(null, null)
            channel.setShowBadge(false)
            channel.importance = NotificationManager.IMPORTANCE_LOW
            channel.description = getString(R.string.channel_description)
            notificationManager.createNotificationChannel(channel)
        }

        notification.setSmallIcon(R.drawable.recommended)
            .setChannelId(CHANNEL_ID)
            .setContentTitle("Image uploading")
            .setContentText("Uploading in progress")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(progressMax, 0, true)

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(
            NOTIFICATION_ID,
            notification.build()
        )
        startForeground(NOTIFICATION_ID, notification.build())

    }

}