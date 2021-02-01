package com.example.graduationproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.places.*
import com.example.graduationproject.repository.PlacesRepository
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "HomeFragmentViewModel"
class HomeFragmentViewModel(private val placesRepository: PlacesRepository): ViewModel() {
    var recommendedPlacesLiveData:LiveData<List<Place>?>? = null

    suspend fun addNewPlace(place: Place, accessToken: String): ResponseMessage?{
        return placesRepository.addNewPlace(place, accessToken)
    }


    suspend fun getRecommendedPlaces(page: Int, accessToken: String): LiveData<List<Place>?>?{
//        if (recommendedPlacesLiveData != null){
//            return recommendedPlacesLiveData
//        }
        recommendedPlacesLiveData = liveData {
            val data = placesRepository.getRecommendedPlaces(page,accessToken)
            emit(data)
        }
        return recommendedPlacesLiveData
    }

    suspend fun searchForPlaceInCountry(
        placeName: String,
        countryName: String,
        accessToken: String
    ): LiveData<List<Place>?> {
        return liveData {
            val data = placesRepository.searchForPlaceInCountry(placeName, countryName, accessToken)
            emit(data)
        }
    }

    suspend fun getUserVisitedPlaces(accessToken: String): LiveData<List<Place>?>{
        return liveData {
            val data = placesRepository.getUserVisitedPlaces(accessToken)
            emit(data)
        }
    }

    suspend fun addPlaceToUserVisitedPlaces(visitedPlace: VisitedPlace, accessToken: String): ResponseMessage?{
        return placesRepository.addPlaceToUserVisitedPlaces(visitedPlace, accessToken)
    }

    suspend fun deleteUserVisitedPlace(placeId: String, accessToken: String): ResponseMessage?{
        return placesRepository.deleteUserVisitedPlace(placeId, accessToken)
    }



}