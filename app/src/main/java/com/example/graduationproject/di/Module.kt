package com.example.graduationproject.di

import android.content.Context
import androidx.room.Room
import com.example.graduationproject.cache.CachingRepository
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.cache.Database
import com.example.graduationproject.network.Api
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.repository.*
import com.example.graduationproject.viewmodels.*
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

val databaseModule = module {
    fun provideDatabase(context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "recommendation_db").build()
    }
    single { provideDatabase(androidContext()) }
}


val authenticationRepositoryModule = module {
    fun provideAuthenticationRepository(context: Context): AuthenticationRepository {
        return AuthenticationRepository(RetrofitInstance.api, context)
    }
    single { provideAuthenticationRepository(androidContext()) }
}

val cachingRepositoryModule = module {
    fun provideCachingRepository(database: Database, context: Context): CachingRepository {
        return CachingRepository(database, context)
    }
    single { provideCachingRepository(get(), androidContext()) }
}

val placesRepositoryModule = module {
    fun providePlacesRepository(context: Context): ProductsRepository {
        return ProductsRepository(RetrofitInstance.api, context)
    }
    single { providePlacesRepository(androidContext()) }
}

val commentsRepositoryModule = module {
    fun provideCommentsRepository(context: Context): CommentsRepository {
        return CommentsRepository(RetrofitInstance.api, context)
    }
    single { provideCommentsRepository(androidContext()) }
}

val ratingRepositoryModule = module {
    fun provideRatingRepository(context: Context): RatingRepository {
        return RatingRepository(RetrofitInstance.api, context)
    }
    single { provideRatingRepository(androidContext()) }
}

val userRepositoryModule = module {
    fun provideUserRepository(context: Context): UserRepository {
        return UserRepository(RetrofitInstance.api, context)
    }
    single { provideUserRepository(androidContext()) }
}

val searchRepositoryModule = module {
    fun provideSearchRepository(context: Context): SearchRepository {
        return SearchRepository(context, api = RetrofitInstance.api)
    }
    single { provideSearchRepository(androidContext()) }
}

val homeFragmentViewModelModule = module { viewModel { HomeFragmentViewModel(get(), get()) } }
val signUpViewModelModule = module { viewModel { SignUpViewModel(get()) } }
val loginViewModelModule = module { viewModel { LoginViewModel(get()) } }
val verificationFragmentViewModelModule =
    module { viewModel { VerificationFragmentViewModel(get()) } }
val placeActivityViewModelModule =
    module { viewModel { ProductActivityViewModel(get(), get(), get()) } }
val userProfileActivityViewModelModule = module { viewModel { UserProfileViewModel(get()) } }
val navigationDrawerViewModelModule = module { viewModel { NavigationDrawerViewModel(get()) } }
val searchViewModelModule = module { viewModel { SearchFragmentViewModel(get()) } }
val cachingViewModelModule = module { viewModel { CachingViewModel(get()) } }

