package com.example.graduationproject.helper

import android.app.Application
import com.example.graduationproject.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
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
                    userProfileActivityViewModelModule
                )
            )
        }
    }
}