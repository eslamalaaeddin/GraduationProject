package com.example.graduationproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.comments.PlaceComment
import com.example.graduationproject.model.comments.UpdateComment
import com.example.graduationproject.repository.CommentsRepository

class PlaceActivityViewModel(private val commentsRepository: CommentsRepository): ViewModel() {

    suspend fun addCommentOnPlace(placeComment: PlaceComment, accessToken: String): ResponseMessage? {
        return commentsRepository.addCommentOnPlace(placeComment, accessToken)
    }

    suspend fun updateCommentOnPlace(placeId: String, placeComment: PlaceComment, accessToken: String): ResponseMessage? {
        return commentsRepository.updateCommentOnPlace(placeId, placeComment, accessToken)
    }

    suspend fun deleteCommentOnPlace(placeId: String, accessToken: String): ResponseMessage? {
        return commentsRepository.deleteCommentOnPlace(placeId, accessToken)
    }
}