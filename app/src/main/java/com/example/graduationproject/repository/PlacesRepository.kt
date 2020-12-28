package com.example.graduationproject.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.places.*
import com.example.graduationproject.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/*
    This repo. is for the second section of our api, which deals with places in general.
 */
private const val TAG = "PlacesRepository"
class PlacesRepository(private val api: Api, private val context: Context): BaseRepository() {

    suspend fun addNewPlace(place: Place, accessToken: String): ResponseMessage{
        return withContext(Dispatchers.IO){
            api.addNewPlace(place, accessToken)
        }
    }


    suspend fun getRecommendedPlaces(page: Int, accessToken: String): List<Place>? {
        var response : List<Place>? = null
        try {
             response = safeApiCall(
                call = { withContext(Dispatchers.IO){api.getRecommendedPlaces(page, accessToken)}},
                errorMessage = "Error Fetching Recommended Places")
        }

        catch (e: IOException) {
            Toast.makeText(context, "${e.message} please check your internet connection", Toast.LENGTH_SHORT).show()
        }
        catch (e: Throwable) {
            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
        }
        return response
    }

    suspend fun searchForPlaceInCountry(placeName: String, countryName: String, accessToken: String): List<Place> {
        return withContext(Dispatchers.IO){
            api.searchForPlaceInCountry(placeName, countryName, accessToken)
        }
    }

    suspend fun searchForSpecificPlace(placeId: Int, accessToken: String): Place {
        return withContext(Dispatchers.IO){
            api.searchForSpecificPlace(placeId, accessToken)
        }
    }

    suspend fun getPlaceImages(placeId: String, accessToken: String): List<PlaceImage>{
        return withContext(Dispatchers.IO){
            api.getPlaceImages(placeId, accessToken)
        }
    }

    suspend fun getPlaceComments(placeId: String, page: Int, accessToken: String): List<Comment>{
        return withContext(Dispatchers.IO){
            api.getPlaceComments(placeId, page, accessToken)
        }
    }

    suspend fun getUserVisitedPlaces(accessToken: String): List<Place>{
        return withContext(Dispatchers.IO){
            api.getUserVisitedPlaces(accessToken)
        }
    }

    suspend fun addPlaceToUserVisitedPlaces(visitedPlace: VisitedPlace, accessToken: String): ResponseMessage{
       return withContext(Dispatchers.IO){
            api.addPlaceToUserVisitedPlaces(visitedPlace, accessToken)
        }
    }

    suspend fun deleteUserVisitedPlace(placeId: String, accessToken: String): ResponseMessage{
        return withContext(Dispatchers.IO){
            api.deleteUserVisitedPlace(placeId, accessToken)
        }
    }

    suspend fun getUserFavoritePlaces(accessToken: String): List<FavoritePlace>{
        return withContext(Dispatchers.IO){
            api.getUserFavoritePlaces(accessToken)
        }
    }

    suspend fun addPlaceToUserFavoritePlaces(favoritePlace: FavoritePlace, accessToken: String): ResponseMessage{
        return withContext(Dispatchers.IO){
            api.addPlaceToUserFavoritePlaces(favoritePlace, accessToken)
        }
    }

    suspend fun deleteUserFavoritePlace(placeId: String, accessToken: String): ResponseMessage{
        return withContext(Dispatchers.IO){
            api.deleteUserFavoritePlace(placeId, accessToken)
        }
    }

}