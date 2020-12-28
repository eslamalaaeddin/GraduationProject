package com.example.graduationproject.network

import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.*
import com.example.graduationproject.model.places.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Api {
    //    @FormUrlEncoded
    @POST("auth/signup")
    suspend fun signUp(@Body signUp: SignUp): Response<ResponseMessage>

    @POST("auth/verify")
    suspend fun verifyUser(@Body verify: Verify): Response<Token>

    @POST("auth/login")
//    fun login(@Body login: Login): Call<Token>
    suspend fun login(@Body login: Login): Response<Token>

    @POST("auth/send_reset_code")
    fun sendResetCode(@Body email: ResetCode): Call<ResponseMessage>

    @POST("auth/refresh_token")
    suspend fun refreshToken(@Body refreshToken: RefreshToken): Response<Token>

    /////////////////////////////////////////////////////////////////////////

    @POST("places")
    suspend fun addNewPlace(
        @Body place: Place,
        @Header("Authorization") accessToken: String
    ): ResponseMessage

    @GET("places/rec")
    suspend fun getRecommendedPlaces(
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Place>>

    @GET("places/search")
    suspend fun searchForPlaceInCountry(
        @Query("key") placeName: String,
        @Query("country") countryName: String,
        @Header("Authorization") accessToken: String
    ): List<Place>


    @GET("places/{id}")
    suspend fun searchForSpecificPlace(
        @Path("id") placeId: Int,
        @Header("Authorization") accessToken: String
    ): Place

    @GET("places/{id}/images")
    suspend fun getPlaceImages(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): List<PlaceImage>

    /*
        POST IMAGES STUFF
     */

    @GET("places/{id}/comments")
    suspend fun getPlaceComments(
        @Path("id") placeId: String,
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): List<Comment>

    /*
        TO BE CHANGED
     */

    @GET("places/visited")
    suspend fun getUserVisitedPlaces(
        @Header("Authorization") accessToken: String
    ): List<Place>

    @POST("places/visited")
    suspend fun addPlaceToUserVisitedPlaces(
        @Body visitedPlace: VisitedPlace,
        @Header("Authorization") accessToken: String
    ): ResponseMessage

    @DELETE("places/visited/{id}")
    suspend fun deleteUserVisitedPlace(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): ResponseMessage

    /*
       TO BE CHANGED AS IT IS HHH
    */
    @GET("places/fav")
    suspend fun getUserFavoritePlaces(
        @Header("Authorization") accessToken: String
    ): List<FavoritePlace>

    @POST("places/visited")
    suspend fun addPlaceToUserFavoritePlaces(
        @Body favoritePlace: FavoritePlace,
        @Header("Authorization") accessToken: String
    ): ResponseMessage

    @DELETE("places/fav/{id}")
    suspend fun deleteUserFavoritePlace(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): ResponseMessage

}