package com.example.graduationproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.comments.PlaceComment
import com.example.graduationproject.model.places.*
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.repository.CommentsRepository
import com.example.graduationproject.repository.PlacesRepository
import com.example.graduationproject.repository.RatingRepository

class PlaceActivityViewModel(
    private val commentsRepository: CommentsRepository,
    private val placesRepository: PlacesRepository,
    private val ratingRepository: RatingRepository
) : ViewModel() {

    suspend fun getPlaceImages(placeId: String, accessToken: String): LiveData<List<PlaceImage>?> {
        return liveData {
            val data = placesRepository.getPlaceImages(placeId, accessToken)
            emit(data)
        }
    }


    suspend fun getPlaceComments(placeId: String, page: Int, accessToken: String): LiveData<List<Comment>?>{
        return liveData {
            val data = placesRepository.getPlaceComments(placeId, page, accessToken)
            emit(data)
        }
    }

    suspend fun getPlaceDetails(placeId: String, accessToken: String): LiveData<Place?>{
        return liveData {
            val data = placesRepository.getPlaceDetails(placeId, accessToken)
            emit(data)
        }
    }


    suspend fun addCommentOnPlace(
        placeComment: PlaceComment,
        accessToken: String
    ): ResponseMessage? {
        return commentsRepository.addCommentOnPlace(placeComment, accessToken)
    }

    suspend fun updateCommentOnPlace(
        commentId: String,
        placeComment: PlaceComment,
        accessToken: String
    ): ResponseMessage? {
        return commentsRepository.updateCommentOnPlace(commentId, placeComment, accessToken)
    }

    suspend fun deleteCommentOnPlace(commentId: String, accessToken: String): ResponseMessage? {
        return commentsRepository.deleteCommentOnPlace(commentId, accessToken)
    }

    suspend fun getUserSpecificRateToPlace(placeId: String, accessToken: String): Rate? {
        return ratingRepository.getUserSpecificRateToPlace(placeId, accessToken)
    }

    suspend fun addRatingToPlace(rate: Rate, placeId: String,accessToken: String): ResponseMessage?{
        return ratingRepository.addRatingToPlace(rate, placeId, accessToken)
    }

    suspend fun updateRatingToPlace( rate: Rate, placeId: String, accessToken: String): ResponseMessage?{
        return ratingRepository.updateRatingToPlace(rate, placeId, accessToken)
    }

    suspend fun getUserFavoritePlaces(accessToken: String): LiveData<List<FavoritePlace>?>{
        return liveData {
            val data = placesRepository.getUserFavoritePlaces(accessToken)
            emit(data)
        }
    }

    suspend fun addPlaceToUserFavoritePlaces(favoritePlace: VisitedPlace, accessToken: String): ResponseMessage?{
        return placesRepository.addPlaceToUserFavoritePlaces(favoritePlace, accessToken)
    }

    suspend fun deleteUserFavoritePlace(placeId: String, accessToken: String): ResponseMessage?{
        return placesRepository.deleteUserFavoritePlace(placeId, accessToken)
    }
}