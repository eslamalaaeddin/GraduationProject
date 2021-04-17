package com.example.graduationproject.viewmodels

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.rating.Rate
import com.example.graduationproject.repository.RatingRepository

class AddPlaceViewModel(private val ratingRepository: RatingRepository): ViewModel() {
    suspend fun addRatingToPlace(rate: Rate, accessToken: String): ResponseMessage?{
        return ratingRepository.addRatingToProduct(rate, "bal", accessToken)
    }

    suspend fun updateRatingToPlace(placeId: String, rate: Rate, accessToken: String): ResponseMessage?{
        return ratingRepository.updateRatingToProduct(rate, placeId, accessToken)
    }
}