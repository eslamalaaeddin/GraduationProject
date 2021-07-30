package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.products.*
import com.example.graduationproject.network.Api
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


private const val TAG = "ProductsRepository"
class ProductsRepository(private val api: Api, private val context: Context, private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): BaseRepository(context) {


    suspend fun getRecommendedProducts(page: Int, accessToken: String): List<Product>? {
        var response : List<Product>? = null
        try {
             response = safeApiCall(
                call = { withContext(ioDispatcher){api.getRecommendedProducts(page, accessToken)}},
                errorMessage = "Error Fetching Recommended Products")
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
                errorMessage = "Error Fetching Recommended Products")
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }

        return response
    }


    suspend fun getProductDetails(productId: String, accessToken: String): Product? {
        var product : Product? = null
        try {
            product = safeApiCall(call = { withContext(ioDispatcher){api.getProductDetails(productId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return product
    }

    suspend fun getProductComments(productId: String, page: Int, accessToken: String): List<Comment>?{
        var productComments : List<Comment>? = null
        try {
            productComments = safeApiCall({ withContext(ioDispatcher){api.getProductComments(productId, page, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return productComments
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

    suspend fun addProductToFavorites(favoriteProduct: PostFavoriteProduct, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall({ withContext(ioDispatcher){api.addProductToUserFavoriteProducts(favoriteProduct, accessToken)} })
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun deleteProductFromFavorites(productId: String, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null
        try {
            responseMessage = safeApiCall({ withContext(ioDispatcher){api.deleteUserFavoriteProduct(productId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

}