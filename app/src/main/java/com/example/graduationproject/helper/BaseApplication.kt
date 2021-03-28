package com.example.graduationproject.helper

import android.app.Application
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.graduationproject.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

const val actionConn = "android.net.conn.CONNECTIVITY_CHANGE"
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
                    navigationDrawerViewModelModule
//                    tokenAuthenticatorModule
                )
            )
        }
//        val intentFilter = IntentFilter(actionConn)
//        val networkStateReceiver = NetworkStateReceiver()
//        registerReceiver(networkStateReceiver, intentFilter)
    }


}