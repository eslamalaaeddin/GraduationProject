package com.example.graduationproject.network

import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.*
import retrofit2.Call
import retrofit2.http.*

interface Api {
//    @FormUrlEncoded
    @POST("auth/signup")
    fun signUp(@Body signUp: SignUp): Call<ResponseMessage>

    @POST("auth/verify")
    fun verify(@Body verify: Verify): Call<Token>

    @POST("auth/login")
    fun login(@Body login: Login): Call<Token>

    @POST("auth/send_reset_code")
    fun sendResetCode(@Body email: ResetCode): Call<ResponseMessage>

}