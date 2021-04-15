package com.example.graduationproject.helper

object Constants {

//    private const val HOST = "http://10.0.3.2:" //GenyMotion
//    private const val HOST = "http://10.0.2.2:" //Emulator
//    private const val url = "http://rec-sys.ddns.net:5000/images/users/user.png"
    //$BASE_USER_IMAGE_URL${comment.userImage}
    private const val HOST = "http://rec-sys.ddns.net:"
    private const val PORT = 5000
    const val BASE_USER_IMAGE_URL = "$HOST$PORT/images/users/"
    const val BASE_PRODUCT_IMAGE_URL = "$HOST$PORT/images/products/"
    const val BASE_URL = "$HOST$PORT/api/" //GenyMotion

    const val TIME_OUT_SECONDS : Long = 60
    const val TIME_OUT_MILLISECONDS : Long = TIME_OUT_SECONDS * 1000

    const val SIGNED_UP_VERIFIED_SIGNED_IN = "signed up verified signed in"
    const val WELCOMED = "welcomed"
    const val ACCESS_TOKEN = "access token"
    const val USER_ID = "userId"
    const val USER_NAME = "userName"
    const val USER_IMAGE_URL = "userImageUrl"
    const val LOGGED_OUT = "loggedOut"
    const val ACCESS_TOKEN_EX_TIME = "access token expiration time"
    const val REFRESH_TOKEN = "refresh token"
    const val REFRESH_TOKEN_EX_TIME = "refresh token expiration time"
    const val USER_EMAIL = "user email"


    const val SEARCH_METHOD = "search method"

    const val CHANNEL_ID = "123"
    const val CHANNEL_NAME = "Image uploading channel"
    const val CHANNEL_DESCRIPTION = "This channel is for user image uploading"
    const val NOTIFICATION_ID = 101
    const val ACTION_IMAGE_UPLOADED = "com.example.graduationproject.imageUploaded"


}