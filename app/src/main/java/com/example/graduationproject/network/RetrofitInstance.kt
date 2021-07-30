package com.example.graduationproject.network

import com.example.graduationproject.helper.Constants.BASE_URL
import com.example.graduationproject.helper.Constants.TIME_OUT_SECONDS
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


//private const val BASE_URL = "http://10.0.2.2:3000/api/" //Emulator
//private const val BASE_URL = "http://10.0.3.2:3000/api/" //GenyMotion
//private const val BASE_URL = "http://192.168.1.3:3000/api/"
//private const val BASE_URL = "http://192.168.1.3:3000/"
//private const val BASE_URL = "http://192.168.236.2:3000/api/"
class RetrofitInstance(){

    companion object {

         val retrofit: Retrofit by lazy {
             val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                 .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                 .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                 .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        val api: Api by lazy {
            retrofit.create(Api::class.java)
        }
    }
}