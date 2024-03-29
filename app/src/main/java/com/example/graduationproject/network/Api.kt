package com.example.graduationproject.network

import com.example.graduationproject.models.ImageResponse
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.authentication.*
import com.example.graduationproject.models.comments.ProductComment
import com.example.graduationproject.models.products.*
import com.example.graduationproject.models.rating.Rate
import com.example.graduationproject.models.user.User
import com.example.graduationproject.models.user.UserName
import com.example.graduationproject.models.user.UserPassword
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @POST("auth/signup")
    suspend fun signUp(@Body signUp: SignUp): Response<ResponseMessage>

    @POST("auth/verify")
    suspend fun verifyUser(@Body verify: Verify): Response<Token>

    @POST("auth/login")
    suspend fun login(@Body login: Login): Response<Token>


    @GET("products/rec")
    suspend fun getRecommendedProducts(
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Product>>


    @GET("products/rec-product")
    suspend fun getRecommendationsByProduct(
        @Query("pid") productId: String,
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Product>>



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
        @Path("id") productId: String,
        @Header("Authorization") accessToken: String
    ): Response<Product>

    @GET("products/{id}/images")
    suspend fun getProductImages(
        @Path("id") productId: String,
        @Header("Authorization") accessToken: String
    ): Response<List<ProductImage>>


    @GET("products/{id}/comments")
    suspend fun getProductComments(
        @Path("id") productId: String,
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<Comment>>


    @GET("products/fav")
    suspend fun getFavoriteProducts(
        @Query("page") page: Int,
        @Header("Authorization") accessToken: String
    ): Response<MutableList<FavoriteProduct>>

    //Here i used PostFavoriteProduct as the body object as it has the field that i want
    @POST("products/fav")
    suspend fun addProductToUserFavoriteProducts(
        @Body favoriteProduct: PostFavoriteProduct,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @DELETE("products/fav/{id}")
    suspend fun deleteUserFavoriteProduct(
        @Path("id") productId: String,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>


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
        @Path("id") productId: String,
        @Header("Authorization") accessToken: String
    ): Response<Rate>

    @POST("ratings/{id}")
    suspend fun addRatingToProduct(
        @Path("id") productId: String,
        @Body rate: Rate,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>

    @PUT("ratings/{id}")
    suspend fun updateRatingToProduct(
        @Path("id") productId: String,
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


    @PUT("user/change_password")
    suspend fun changeUserPassword(
        @Body userPassword: UserPassword,
        @Header("Authorization") accessToken: String
    ): Response<ResponseMessage>


    //ImageUploading

    @Multipart
    @PUT("user/change_image")
    fun uploadImage(
        @Part image: MultipartBody.Part?,
        @Header("Authorization") accessToken: String,
        @Part("description") description: RequestBody,
        @Query("old-image") oldImageName : String
    ): Call<ImageResponse?>?
}