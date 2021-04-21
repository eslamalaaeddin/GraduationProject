package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.products.*
import com.example.graduationproject.network.Api
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/*
    This repo. is for the second section of our api, which deals with places in general.
 */
private const val TAG = "ProductsRepository"
class ProductsRepository(private val api: Api, private val context: Context, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): BaseRepository(context) {


//    suspend fun addNewPlace(product: Product, accessToken: String): ResponseMessage?{
//        var responseMessage : ResponseMessage? = null
//        try {
//            responseMessage = safeApiCall(call = { withContext(Dispatchers.IO){api.addNewPlace(product, accessToken)}})
//        }
//
//        catch (ex: Throwable) {
//            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
//            else{exceptionHandler.handleException(ex)}
//        }
//        return responseMessage
//    }

    suspend fun getRecommendedProducts(page: Int, accessToken: String): List<Product>? {
        var response : List<Product>? = null
        try {
             response = safeApiCall(
                call = { withContext(ioDispatcher){api.getRecommendedPlaces(page, accessToken)}},
                errorMessage = "Error Fetching Recommended Places")
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }

        return response
    }


    suspend fun getRecommendedProductsByProduct(productId: String, page: Int, accessToken: String): List<Product>? {
        var response : List<Product>? = null
        try {
            response = safeApiCall(
                call = { withContext(ioDispatcher){api.getRecommendationsByProduct(productId, page, accessToken)}},
                errorMessage = "Error Fetching Recommended Places")
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }

        return response
    }


    suspend fun getProductDetails(placeId: String, accessToken: String): Product? {
        var product : Product? = null
        try {
            product = safeApiCall(call = { withContext(ioDispatcher){api.getProductDetails(placeId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return product
    }

    suspend fun getPlaceImages(placeId: String, accessToken: String): List<ProductImage>?{
        var productImages : List<ProductImage>? = null
        try {
            productImages = safeApiCall(call = {withContext(ioDispatcher){api.getPlaceImages(placeId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return productImages
    }

    suspend fun getProductComments(placeId: String, page: Int, accessToken: String): List<Comment>?{
        var placeComments : List<Comment>? = null
        try {
            placeComments = safeApiCall({ withContext(ioDispatcher){api.getProductComments(placeId, page, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return placeComments
    }

    suspend fun getFavoriteProducts(page: Int, accessToken: String): MutableList<FavoriteProduct>?{
        var favoriteProducts : MutableList<FavoriteProduct>? = null
        try {
            favoriteProducts = safeApiCall({ withContext(ioDispatcher){api.getFavoriteProducts(page, accessToken)} })
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return favoriteProducts
    }

    suspend fun addProductToFavorites(favoriteProduct: VisitedProduct, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall({ withContext(ioDispatcher){api.addPlaceToUserFavoritePlaces(favoriteProduct, accessToken)} })
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun deleteProductFromFavorites(placeId: String, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null
        try {
            responseMessage = safeApiCall({ withContext(ioDispatcher){api.deleteUserFavoritePlace(placeId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

}