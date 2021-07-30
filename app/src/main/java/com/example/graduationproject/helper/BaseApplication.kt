package com.example.graduationproject.helper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.example.graduationproject.R
import com.example.graduationproject.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

private const val CHANNEL_ID = "123"
private const val CHANNEL_NAME = "channel name"

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BaseApplication)
            modules(
                //List of modules declared in Module class
                listOf(
                    apiModule,
                    placesRepositoryModule,
                    homeFragmentViewModelModule,
                    authenticationRepositoryModule,
                    commentsRepositoryModule,
                    signUpViewModelModule,
                    loginViewModelModule,
                    verificationFragmentViewModelModule,
                    placeActivityViewModelModule,
                    ratingRepositoryModule,
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

        val appSettingPrefs: SharedPreferences =
            getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    companion object {
        var context: Context? = null
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

