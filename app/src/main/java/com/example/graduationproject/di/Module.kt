package com.example.graduationproject.di

import com.example.graduationproject.network.Api
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.repository.PlacesRepository
import com.example.graduationproject.viewmodel.HomeFragmentViewModel
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

val placesRepository = module {
    fun providePlacesRepository(): PlacesRepository{
        return PlacesRepository(RetrofitInstance.api)
    }
    single { providePlacesRepository() }
}

val homeFragmentViewModelModule = module{viewModel { HomeFragmentViewModel (get()) }}

