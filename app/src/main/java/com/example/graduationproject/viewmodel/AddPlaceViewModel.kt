package com.example.graduationproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.repository.RatingRepository

class AddPlaceViewModel(private val ratingRepository: RatingRepository): ViewModel() {
    suspend fun addRatingToPlace(rate: Rate, accessToken: String): ResponseMessage?{
        return ratingRepository.addRatingToPlace(rate, accessToken)
    }

    suspend fun updateRatingToPlace(placeId: String, rate: Rate, accessToken: String): ResponseMessage?{
        return ratingRepository.updateRatingToPlace(placeId, rate, accessToken)
    }
}