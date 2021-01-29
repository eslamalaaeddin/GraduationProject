package com.example.graduationproject.repository

import android.content.Context
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.places.*
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/*
    This repo. is for the second section of our api, which deals with places in general.
 */
private const val TAG = "PlacesRepository"
class PlacesRepository(private val api: Api, private val context: Context): BaseRepository(context) {

    suspend fun addNewPlace(place: Place, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null
        try {
            responseMessage = safeApiCall(call = { withContext(Dispatchers.IO){api.addNewPlace(place, accessToken)}})
        }

        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun getRecommendedPlaces(page: Int, accessToken: String): List<Place>? {
        var response : List<Place>? = null
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

    suspend fun searchForPlaceInCountry(placeName: String, countryName: String, accessToken: String): List<Place>? {
        var places: List<Place>? = null
        try {
            places = safeApiCall(call = { withContext(Dispatchers.IO){api.searchForPlaceInCountry(placeName, countryName, accessToken)} })
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return places
    }

    suspend fun getPlaceDetails(placeId: String, accessToken: String): Place? {
        var place : Place? = null
        try {
            place = safeApiCall(call = { withContext(Dispatchers.IO){api.getPlaceDetails(placeId, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return place
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

    suspend fun getPlaceComments(placeId: String, page: Int, accessToken: String): List<Comment>?{
        var placeComments : List<Comment>? = null
        try {
            placeComments = safeApiCall({ withContext(Dispatchers.IO){api.getPlaceComments(placeId, page, accessToken)}})
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return placeComments
    }

    suspend fun getUserVisitedPlaces(accessToken: String): List<Place>?{
        var visitedPlaces : List<Place>? = null
        try {
            visitedPlaces = safeApiCall({ withContext(Dispatchers.IO){api.getUserVisitedPlaces(accessToken)} })
        }

        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }

        return visitedPlaces
    }

    suspend fun addPlaceToUserVisitedPlaces(visitedPlace: VisitedPlace, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null
        try {
            responseMessage = safeApiCall({ withContext(Dispatchers.IO){api.addPlaceToUserVisitedPlaces(visitedPlace, accessToken)} })
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
       return responseMessage
    }

    suspend fun deleteUserVisitedPlace(placeId: String, accessToken: String): ResponseMessage?{
        var responseMessage : ResponseMessage? = null
        try {
            responseMessage = safeApiCall({ withContext(Dispatchers.IO){api.deleteUserVisitedPlace(placeId, accessToken)} })
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return responseMessage
    }

    suspend fun getUserFavoritePlaces(accessToken: String): List<FavoritePlace>?{
        var favoritePlaces : List<FavoritePlace>? = null
        try {
            favoritePlaces = safeApiCall({ withContext(Dispatchers.IO){api.getUserFavoritePlaces(accessToken)} })
        }
        catch (ex: Throwable) {
            if (ex is HttpException){exceptionHandler.handleException(ex, ex.code().toString())}
            else{exceptionHandler.handleException(ex)}
        }
        return favoritePlaces
    }

    suspend fun addPlaceToUserFavoritePlaces(favoritePlace: VisitedPlace, accessToken: String): ResponseMessage?{
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

    suspend fun deleteUserFavoritePlace(placeId: String, accessToken: String): ResponseMessage?{
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