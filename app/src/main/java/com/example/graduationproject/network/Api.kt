package com.example.graduationproject.network

import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.*
import com.example.graduationproject.model.comments.PlaceComment
import com.example.graduationproject.model.places.*
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.model.user.User
import com.example.graduationproject.model.user.UserName
import com.example.graduationproject.model.user.UserPassword
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    ): Response<ResponseMessage>

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
    ): Response<List<Place>>


    @GET("places/{id}")
    suspend fun getPlaceDetails(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<Place>

    @GET("places/{id}/images")
    suspend fun getPlaceImages(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<List<PlaceImage>>

    /*
        POST IMAGES STUFF
     */

    @GET("places/{id}/comments")
    suspend fun getPlaceComments(
        @Path("id") placeId: String,
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Comment>>

    /*
        TO BE CHANGED
     */

    @GET("places/visited")
    suspend fun getUserVisitedPlaces(
        @Header("Authorization") accessToken: String
    ): Response<List<Place>>

    @POST("places/visited")
    suspend fun addPlaceToUserVisitedPlaces(
        @Body visitedPlace: VisitedPlace,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @DELETE("places/visited/{id}")
    suspend fun deleteUserVisitedPlace(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    /*
       TO BE CHANGED AS IT IS HHH
    */
    @GET("places/fav")
    suspend fun getUserFavoritePlaces(
        @Header("Authorization") accessToken: String
    ): Response<List<FavoritePlace>>

    @POST("places/visited")
    suspend fun addPlaceToUserFavoritePlaces(
        @Body favoritePlace: FavoritePlace,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @DELETE("places/fav/{id}")
    suspend fun deleteUserFavoritePlace(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    /////////////////////////////////////////////////////////////////////////////

//    @FormUrlEncoded
//    @POST("comments")
//    suspend fun addCommentOnPlace(
//        @Field("pid") pid: Int,
//        @Field("comment") comment: String,
//        @Field("time") time: String = "2020-09-25 20:13:00",
//        @Header("Authorization") accessToken: String
//    ): ResponseMessage

    @POST("comments")
    suspend fun addCommentOnPlace(
        @Body placeComment: PlaceComment,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @PUT("comments/{id}")
    suspend fun updateCommentOnPlace(
        @Path("id") placeId: String,
        @Body placeComment: PlaceComment,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @DELETE("comments/{id}")
    suspend fun deleteCommentOnPlace(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    //////////////////////////////////////////////////////////////////////////////////

    @POST("ratings")
    suspend fun addRatingToPlace(
        @Body rate: Rate,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @PUT("ratings/{id}")
    suspend fun updateRatingToPlace(
        @Path("id") ratingId: String,
        @Body rate: Rate,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    ///////////////////////////////////////////////////////////////////////////////////

    @GET("user")
    suspend fun getUser(@Header("Authorization") accessToken: String): Response<User>

    @PUT("user")
    suspend fun updateUserName(
        @Body userName: UserName,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    /*
        Change user image
     */

    @PUT("user/change_password")
    suspend fun updateUserPassword(
        @Body userPassword: UserPassword,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    /////////////////////////////////////////////////////////////////////////////////////

    @Multipart
    @POST("places/{id}/images")
    fun uploadImage(
        @Part part: MultipartBody.Part?,
        @Path("id") placeId: String?,
        @Header("Authorization") accessToken: String
    ): Call<RequestBody?>?
}