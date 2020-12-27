package com.example.graduationproject.helper

import android.app.Application
import com.example.graduationproject.di.apiModule
import com.example.graduationproject.di.homeFragmentViewModelModule
import com.example.graduationproject.di.placesRepository
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
                    placesRepository,
                    homeFragmentViewModelModule
                )
            )
        }
    }
}