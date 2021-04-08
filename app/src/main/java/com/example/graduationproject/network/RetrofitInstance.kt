package com.example.graduationproject.network

import android.content.Context
import com.example.graduationproject.helper.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//private const val BASE_URL = "http://10.0.2.2:3000/api/" //Emulator
//private const val BASE_URL = "http://10.0.3.2:3000/api/" //GenyMotion
//private const val BASE_URL = "http://192.168.1.3:3000/api/"
//private const val BASE_URL = "http://192.168.1.3:3000/"
//private const val BASE_URL = "http://192.168.236.2:3000/api/"
class RetrofitInstance(){

    companion object {

//        private lateinit var context: Context
//
//        fun setContext(con: Context) {
//            context=con
//        }

         val retrofit by lazy {

//             val interceptor =  HttpLoggingInterceptor()
//             interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//             val client =  OkHttpClient.Builder().addInterceptor(interceptor).build()
//             val client =  OkHttpClient.Builder().authenticator(TokenAuthenticator(context)).build()
//
//                 .addInterceptor(interceptor).build()
//             client.authenticator = TokenAuthenticator(context = )

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(Api::class.java)
        }
    }
}