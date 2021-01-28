package com.example.graduationproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.comments.PlaceComment
import com.example.graduationproject.model.places.Comment
import com.example.graduationproject.model.places.Place
import com.example.graduationproject.model.places.PlaceImage
import com.example.graduationproject.repository.CommentsRepository
import com.example.graduationproject.repository.PlacesRepository

class PlaceActivityViewModel(
    private val commentsRepository: CommentsRepository,
    private val placesRepository: PlacesRepository
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
        placeId: String,
        placeComment: PlaceComment,
        accessToken: String
    ): ResponseMessage? {
        return commentsRepository.updateCommentOnPlace(placeId, placeComment, accessToken)
    }

    suspend fun deleteCommentOnPlace(placeId: String, accessToken: String): ResponseMessage? {
        return commentsRepository.deleteCommentOnPlace(placeId, accessToken)
    }
}