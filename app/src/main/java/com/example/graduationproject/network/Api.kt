package com.example.graduationproject.network

import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.*
import com.example.graduationproject.model.comments.ProductComment
import com.example.graduationproject.model.products.*
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.model.user.User
import com.example.graduationproject.model.user.UserName
import com.example.graduationproject.model.user.UserPassword
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import okhttp3.ResponseBody

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

    @POST("auth/refresh_token")
    fun refreshTheToken(@Body refreshToken: RefreshToken): Call<Token>

    @GET("products/rec")
    suspend fun getRecommendedPlaces(
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Product>>

 //   http://localhost:3000/api/products?s=story&page=1
//http://localhost:3000/api/products?t=drama&page=1
    @GET("products")
    suspend fun searchByProductName(
        @Query("s") productName: String,
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Product>>


    @GET("products")
    suspend fun searchByProductTag(
        @Query("t") productTag: String,
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Product>>


    @GET("products/{id}")
    suspend fun getProductDetails(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<Product>

    @GET("products/{id}/images")
    suspend fun getPlaceImages(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<List<ProductImage>>


    //it was suspend and i modified it
    @GET("products/{id}/comments")
    suspend fun getProductComments(
        @Path("id") placeId: String,
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Comment>>


    @GET("products/fav")
    suspend fun getFavoriteProducts(
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<MutableList<FavoriteProduct>>

    //Here i used VisitedProduct as the body object as it has the field that i want
    @POST("products/fav")
    suspend fun addPlaceToUserFavoritePlaces(
        @Body favoriteProduct: VisitedProduct,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @DELETE("products/fav/{id}")
    suspend fun deleteUserFavoritePlace(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>




//    @POST("comments")
//    suspend fun addCommentOnProduct(
//        @Body productComment: ProductComment,
//        @Header("Authorization") accessToken: String
//    ): Response<ResponseMessage>

    @POST("comments")
    suspend fun addCommentOnProduct(
        @Body productComment: ProductComment,
        @Header("Authorization") accessToken: String
    ): Response<ReturnedComment>

    @PUT("comments/{id}")
    suspend fun updateCommentOnProduct(
        @Path("id") commentId: String,
        @Body productComment: ProductComment,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @DELETE("comments/{id}")
    suspend fun deleteCommentFromProduct(
        @Path("id") commentId: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    //////////////////////////////////////////////////////////////////////////////////

    @GET("ratings/{id}")
    suspend fun getUserSpecificRate(
        @Path("id") placeId: String,
        @Header("Authorization") accessToken: String
    ): Response<Rate>

    @POST("ratings/{id}")
    suspend fun addRatingToPlace(
        @Path("id") placeId: String,
        @Body rate: Rate,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @PUT("ratings/{id}")
    suspend fun updateRatingToPlace(
        @Path("id") placeId: String,
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

    @Multipart
    @POST("user/change_image")
    fun changeUserImage(
        @Part image: MultipartBody.Part?,
        @Header("Authorization") accessToken: String,
        @Part("description") description: RequestBody
    ): Call<ResponseBody?>?

    @PUT("user/change_password")
    suspend fun changeUserPassword(
        @Body userPassword: UserPassword,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    /////////////////////////////////////////////////////////////////////////////////////

//    @Multipart
//    @POST("places/{id}/images")
//    fun uploadImage(
//        @Part part: MultipartBody.Part?,
//        @Path("id") placeId: String?,
//        @Header("Authorization") accessToken: String
//    ): Call<RequestBody?>?

    @Multipart
    @PUT("user/change_image")
    fun uploadImage(
        @Part image: MultipartBody.Part?,
        @Header("Authorization") accessToken: String,
        @Part("description") description: RequestBody
    ): Call<ResponseBody?>?
}