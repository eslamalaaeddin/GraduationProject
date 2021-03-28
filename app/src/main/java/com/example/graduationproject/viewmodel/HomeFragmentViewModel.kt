package com.example.graduationproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.graduationproject.model.products.*
import com.example.graduationproject.repository.PlacesRepository

private const val TAG = "HomeFragmentViewModel"
class HomeFragmentViewModel(private val placesRepository: PlacesRepository): ViewModel() {
    var recommendedPlacesLiveData:LiveData<List<Product>?>? = null

//    suspend fun addNewPlace(product: Product, accessToken: String): ResponseMessage?{
//        return placesRepository.addNewPlace(product, accessToken)
//    }


    suspend fun getRecommendedProducts(page: Int, accessToken: String): LiveData<List<Product>?>?{
//        if (recommendedPlacesLiveData != null){
//            return recommendedPlacesLiveData
//        }
        recommendedPlacesLiveData = liveData {
            val data = placesRepository.getRecommendedPlaces(page,accessToken)
            emit(data)
        }
        return recommendedPlacesLiveData
    }

//    suspend fun searchForPlaceInCountry(
//        placeName: String,
//        countryName: String,
//        accessToken: String
//    ): LiveData<List<Product>?> {
//        return liveData {
//            val data = placesRepository.searchForPlaceInCountry(placeName, countryName, accessToken)
//            emit(data)
//        }
//    }

//    suspend fun getUserVisitedPlaces(accessToken: String): LiveData<List<Product>?>{
//        return liveData {
//            val data = placesRepository.getUserVisitedPlaces(accessToken)
//            emit(data)
//        }
//    }

//    suspend fun addPlaceToUserVisitedPlaces(visitedPlace: VisitedProduct, accessToken: String): ResponseMessage?{
//        return placesRepository.addPlaceToUserVisitedPlaces(visitedPlace, accessToken)
//    }
//
//    suspend fun deleteUserVisitedPlace(placeId: String, accessToken: String): ResponseMessage?{
//        return placesRepository.deleteUserVisitedPlace(placeId, accessToken)
//    }



}