package com.example.graduationproject.helpers

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.graduationproject.R
import com.example.graduationproject.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        RetrofitInstance.setContext(applicationContext)
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
                    searchViewModelModule
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

}