package com.example.graduationproject.model.authentication

import com.example.graduationproject.model.ResponseWrapper

data class Token(
    val access_token: String,
    val access_token_exp: String,
    val refresh_token: String,
    val refresh_token_exp: String,
    var responseCode: Int? = null
)