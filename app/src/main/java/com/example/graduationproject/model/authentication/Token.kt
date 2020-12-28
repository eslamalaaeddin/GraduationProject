package com.example.graduationproject.model.authentication

data class Token(
    val access_token: String,
    val access_token_exp: String,
    val refresh_token: String,
    val refresh_token_exp: String
)