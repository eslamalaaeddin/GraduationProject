package com.example.graduationproject.helper

object Constants {
    private const val PORT = 5000
    private const val HOST = "http://10.0.3.2:"
    const val BASE_USER_IMAGE_URL = "$HOST$PORT/images/users/"
    const val BASE_PRODUCT_IMAGE_URL = "$HOST$PORT/images/products/"
    const val BASE_URL = "$HOST$PORT/api/" //GenyMotion
    //const val BASE_URL = "$HOST$PORT/api/" //Emulator


    ///Splach
    const val accessTokenExpirationTime = "Sun, 27 Dec 2020 15:42:25 GMT"
    const val refreshTokenExpirationTime = "Sat, 12 Dec 2020 15:27:25 GMT"
    const val MINUTES_15 = 900
    const val DAYS_14 = 1209600


    const val SIGNED_UP_VERIFIED_SIGNED_IN = "signed up verified signed in"
    const val WELCOMED = "welcomed"
    const val ACCESS_TOKEN = "access token"
    const val USER_ID = "userId"
    const val LOGGED_OUT = "loggedOut"
    const val ACCESS_TOKEN_EX_TIME = "access token expiration time"
    const val REFRESH_TOKEN = "refresh token"
    const val REFRESH_TOKEN_EX_TIME = "refresh token expiration time"
    const val USER_EMAIL = "user email"


}