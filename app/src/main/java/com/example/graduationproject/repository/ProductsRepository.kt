package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.products.*
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/*
    This repo. is for the second section of our api, which deals with places in general.
 */
private const val TAG = "PlacesRepository"
class PlacesRepository(private val api: Api, private val context: Context): BaseRepository(context) {

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

    suspend fun getRecommendedPlaces(page: Int, accessToken: String): List<Product>? {
        var response : List<Product>? = null
        try {
             response = safeApiCall(
                call = { withContext(Dispatchers.IO){api.getRecommendedPlaces(page, accessToken)}},
                errorMessage = "Error Fetching Recommended Places")
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }

        return response
    }

//    suspend fun searchForPlaceInCountry(placeName: String, countryName: String, accessToken: String): List<Product>? {
//        var products: List<Product>? = null
//        try {
//            products = safeApiCall(call = { withContext(Dispatchers.IO){api.searchForPlaceInCountry(placeName, countryName, accessToken)} })
//        }
//        catch (ex: Throwable) {
//            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
//            else{exceptionHandler.handleException(ex)}
//        }
//        return products
//    }

    suspend fun getProductDetails(placeId: String, accessToken: String): Product? {
        var product : Product? = null
        try {
            product = safeApiCall(call = { withContext(Dispatchers.IO){api.getProductDetails(placeId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return product
    }

    suspend fun getPlaceImages(placeId: String, accessToken: String): List<PlaceImage>?{
        var placeImages : List<PlaceImage>? = null
        try {
            placeImages = safeApiCall(call = {withContext(Dispatchers.IO){api.getPlaceImages(placeId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return placeImages
    }

    suspend fun getProductComments(placeId: String, page: Int, accessToken: String): List<Comment>?{
        var placeComments : List<Comment>? = null
        try {
            placeComments = safeApiCall({ withContext(Dispatchers.IO){api.getProductComments(placeId, page, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return placeComments
    }

//    suspend fun getUserVisitedPlaces(accessToken: String): List<Product>?{
//        var visitedProducts : List<Product>? = null
//        try {
//            visitedProducts = safeApiCall({ withContext(Dispatchers.IO){api.getUserVisitedPlaces(accessToken)} })
//        }
//
//        catch (ex: Throwable) {
//            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
//            else{exceptionHandler.handleException(ex)}
//        }
//
//        return visitedProducts
//    }

//    suspend fun addPlaceToUserVisitedPlaces(visitedPlace: VisitedPlace, accessToken: String): ResponseMessage?{
//        var responseMessage : ResponseMessage? = null
//        try {
//            responseMessage = safeApiCall({ withContext(Dispatchers.IO){api.addPlaceToUserVisitedPlaces(visitedPlace, accessToken)} })
//        }
//        catch (ex: Throwable) {
//            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
//            else{exceptionHandler.handleException(ex)}
//        }
//       return responseMessage
//    }

//    suspend fun deleteUserVisitedPlace(placeId: String, accessToken: String): ResponseMessage?{
//        var responseMessage : ResponseMessage? = null
//        try {
//            responseMessage = safeApiCall({ withContext(Dispatchers.IO){api.deleteUserVisitedPlace(placeId, accessToken)} })
//        }
//        catch (ex: Throwable) {
//            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
//            else{exceptionHandler.handleException(ex)}
//        }
//        return responseMessage
//    }

    suspend fun getFavoriteProducts(accessToken: String): List<FavoritePlace>?{
        var favoritePlaces : List<FavoritePlace>? = null
        try {
            favoritePlaces = safeApiCall({ withContext(Dispatchers.IO){api.getFavoriteProducts(accessToken)} })
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return favoritePlaces
    }

    suspend fun addProductToFavorites(favoritePlace: VisitedPlace, accessToken: String): ResponseMessage?{
        var responseMessage: ResponseMessage? = null
        try {
            responseMessage = safeApiCall({ withContext(Dispatchers.IO){api.addPlaceToUserFavoritePlaces(favoritePlace, accessToken)} })
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
            responseMessage = safeApiCall({ withContext(Dispatchers.IO){api.deleteUserFavoritePlace(placeId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

}