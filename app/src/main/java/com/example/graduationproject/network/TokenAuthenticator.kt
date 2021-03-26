package com.example.graduationproject.network

import android.content.Context
import android.util.Log
import com.example.graduationproject.model.authentication.RefreshToken
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.ui.activities.SplashActivity
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import retrofit2.Callback

private const val TAG = "TokenAuthenticator"
class TokenAuthenticator(private val context: Context) : Authenticator {
    private val retrofitInstance = RetrofitInstance.retrofit
    val api = retrofitInstance.create(Api::class.java)

    override fun authenticate(route: Route?, response: Response): Request? {
        val oldToken = SplashActivity.getAccessToken(context)
        val refreshToken = SplashActivity.getRefreshToken(context)
        if (response.code == 401) {
            val refreshCall: Call<Token> = api.refreshTheToken(RefreshToken(oldToken, refreshToken))

            refreshCall.enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: retrofit2.Response<Token>) {
                    Log.d(TAG, "IIII onResponse: $response")
                    Log.d(TAG, "IIII onResponse: ${response.body()}")
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Log.d(TAG, "IIII onFailure: ")
                }
            })

//            return if (refreshResponse != null && refreshResponse.code() === 200) {
//                //read new JWT value from response body or interceptor depending upon your JWT availability logic
//                newCookieValue = readNewJwtValue()
//                response.request.newBuilder()
//                    .header("basic-auth", newCookieValue)
//                    .build()
//            } else {
//                null
//            }
        }
        return null
    }
}