package com.example.graduationproject.helper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.graduationproject.R
import com.example.graduationproject.di.*
import com.example.graduationproject.models.notification.Notifier
import com.example.graduationproject.ui.activities.ProductActivity
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import kotlin.random.Random

private const val CHANNEL_ID = "123"
private const val CHANNEL_NAME = "channel name"

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        createNotificationChannel()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BaseApplication)
            modules(
                listOf(
                    apiModule,
                    placesRepositoryModule,
                    homeFragmentViewModelModule,
                    authenticationRepositoryModule,
                    commentsRepositoryModule,
                    signUpViewModelModule,
                    loginViewModelModule,
                    verificationFragmentViewModelModule,
                    splashActivityViewModelModule,
                    placeActivityViewModelModule,
                    ratingRepositoryModule,
                    addPlaceViewModelModule,
                    userRepositoryModule,
                    userProfileActivityViewModelModule,
                    navigationDrawerViewModelModule,
                    searchRepositoryModule,
                    searchViewModelModule,
                    databaseModule,
                    cachingRepositoryModule,
                    cachingViewModelModule
                )
            )
        }
//        val intentFilter = IntentFilter(actionConn)
//        val networkStateReceiver = GlobalNetworkStateReceiver()
//        registerReceiver(networkStateReceiver, intentFilter)

        val appSettingPrefs: SharedPreferences =
            getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }


    private fun createNotificationChannel() {
        //1 Create the channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = context?.getString(R.string.channel_description)

            val notificationManager: NotificationManager? =
                context?.getSystemService(NotificationManager::class.java)

            notificationManager?.createNotificationChannel(channel)
        }
    }

    //com.example.graduationproject.models.notification.Notification provider
    companion object {
        var context: Context? = null
        var destination: Class<*>? = null

        fun fireClientSideNotification(
            notifier: Notifier
        ) {

            val remoteView = RemoteViews(context?.packageName, R.layout.custom_notification_layout)

            ////////////////////// CUSTOMIZING THE NOTIFICATION //////////////////////////////
            remoteView.setTextViewText(
                R.id.notificationContentTextView,
                "${notifier.name} recommended a movie for you."
            )
            destination = ProductActivity::class.java
            remoteView.setImageViewBitmap(R.id.notificationImageView, notifier.imageBitmap)

            val sound: Uri =
                Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.facebook_notification_sound)

            //2 Create the builder
            val builder = NotificationCompat.Builder(context!!, CHANNEL_NAME)
                .setSmallIcon(R.drawable.recommended)
//                .setContentTitle(NOTIFICATION_TITLE)
//                .setContentText(NOTIFICATION_CONTENT)
                .setCustomContentView(remoteView)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(sound)
                .setVibrate(longArrayOf(0, 250, 100, 250))
                .setAutoCancel(true)

            //3 Create the action
            val actionIntent = Intent(context, destination)


            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    actionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            builder.setContentIntent(pendingIntent)

            //4 Issue the notification
            val notificationManager =
                NotificationManagerCompat.from(context!!)
            notificationManager.notify(Random.nextInt(), builder.build())
        }

        fun getConnectionType(): Int {
            var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm?.run {
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        when {
                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                                result = 2
                            }
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                                result = 1
                            }
                            hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                                result = 3
                            }
                        }
                    }
                }
            } else {
                cm?.run {
                    cm.activeNetworkInfo?.run {
                        when (type) {
                            ConnectivityManager.TYPE_WIFI -> {
                                result = 2
                            }
                            ConnectivityManager.TYPE_MOBILE -> {
                                result = 1
                            }
                            ConnectivityManager.TYPE_VPN -> {
                                result = 3
                            }
                        }
                    }
                }
            }
            return result
        }

    }



}

