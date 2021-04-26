package com.example.graduationproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Utils.getAccessToken
import com.example.graduationproject.ui.activities.SplashActivity

private const val TAG = "TokenActivity"
class TokenActivity : AppCompatActivity() {

    private lateinit var accessToken: String
    private lateinit var requestButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
        requestButton = findViewById(R.id.requestButton)
        accessToken = getAccessToken(this).orEmpty()

        requestButton.setOnClickListener {
            showImageUploadingProgress()
        }

    }

    private fun showImageUploadingProgress() {
        val progressMax = 100
        val notificationManager =
            NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID, Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setSound(null, null)
            channel.setShowBadge(false)
            channel.importance = NotificationManager.IMPORTANCE_LOW
            channel.description = getString(R.string.channel_description)
            notificationManager.createNotificationChannel(channel)
        }


        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_NAME)
            .setSmallIcon(R.drawable.ic_splash_logo)
            .setChannelId(Constants.CHANNEL_ID)
            .setContentTitle("Image uploading")
            .setContentText("Uploading in progress")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(progressMax, 0, true)
        notificationManager.notify(2, notification.build())

        Thread {
            //SystemClock.sleep(2000);
            var progress = 0
            while (progress <= progressMax) {
                notification.setProgress(progressMax, progress, false)
                notificationManager.notify(2, notification.build())
                SystemClock.sleep(500)
                progress += 10
            }
            notification.setContentText("Done!")
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(2, notification.build())
        }.start()
    }

}