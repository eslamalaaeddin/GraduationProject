package com.example.graduationproject.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//private const val BASE_URL = "http://10.0.2.2:3000/api/" //Emulator
private const val BASE_URL = "http://10.0.3.2:3000/api/" //GenyMotion
//private const val BASE_URL = "http://192.168.1.3:3000/api/"
//private const val BASE_URL = "http://192.168.1.3:3000/"
//private const val BASE_URL = "http://192.168.236.2:3000/api/"
class RetrofitInstance {
    companion object {
         val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(Api::class.java)
        }
    }
}