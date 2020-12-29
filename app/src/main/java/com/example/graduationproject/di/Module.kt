package com.example.graduationproject.di

import android.content.Context
import com.example.graduationproject.network.Api
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.repository.AuthenticationRepository
import com.example.graduationproject.repository.CommentsRepository
import com.example.graduationproject.repository.PlacesRepository
import com.example.graduationproject.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
private lateinit var api: Api

val apiModule = module {
    fun provideApi(): Api {
        val retrofitInstance = RetrofitInstance.retrofit
        api = retrofitInstance.create(Api::class.java)
        return api
    }
    single { provideApi() }
}



val authenticationRepositoryModule = module {
    fun provideAuthenticationRepository(context: Context): AuthenticationRepository{
        return AuthenticationRepository(RetrofitInstance.api, context)
    }
    single { provideAuthenticationRepository(androidContext()) }
}

val placesRepositoryModule = module {
    fun providePlacesRepository(context: Context): PlacesRepository{
        return PlacesRepository(RetrofitInstance.api, context)
    }
    single { providePlacesRepository(androidContext()) }
}

val commentsRepositoryModule = module {
    fun provideCommentsRepository(context: Context): CommentsRepository{
        return CommentsRepository(RetrofitInstance.api, context)
    }
    single { provideCommentsRepository(androidContext()) }
}

val homeFragmentViewModelModule = module{viewModel { HomeFragmentViewModel (get()) }}
val signUpViewModelModule = module { viewModel { SignUpViewModel(get()) } }
val loginViewModelModule = module { viewModel { LoginViewModel(get()) } }
val verificationFragmentViewModelModule = module { viewModel { VerificationFragmentViewModel(get()) } }
val splashActivityViewModelModule = module { viewModel { SplashActivityViewModel(get()) } }
val placeActivityViewModelModule = module { viewModel { PlaceActivityViewModel(get()) } }

