package com.example.graduationproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.comments.ProductComment
import com.example.graduationproject.model.products.*
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.repository.CommentsRepository
import com.example.graduationproject.repository.PlacesRepository
import com.example.graduationproject.repository.RatingRepository

class ProductActivityViewModel(
    private val commentsRepository: CommentsRepository,
    private val placesRepository: PlacesRepository,
    private val ratingRepository: RatingRepository
) : ViewModel() {
    var commentsLiveData : LiveData<List<Comment>?>? = null
    var productImagesLiveData: LiveData<List<ProductImage>?>? = null
    var productLiveData : LiveData<Product?>? = null
    var favoritePlacesLiveData: LiveData<List<FavoriteProduct>?>? = null
    var userSpecificPlaceRate: Rate? = null

//    suspend fun getPlaceImages(placeId: String, accessToken: String): LiveData<List<ProductImage>?>? {
//        if (productImagesLiveData != null) {
//            return productImagesLiveData
//        }
//        productImagesLiveData = liveData {
//            val data = placesRepository.getPlaceImages(placeId, accessToken)
//            emit(data)
//        }
//        return productImagesLiveData
//    }

    suspend fun getProductComments(placeId: String, page: Int, accessToken: String): LiveData<List<Comment>?>?{
        // i hide it as i want comment to be real time
//        if (commentsLiveData != null) {
//            return commentsLiveData
//        }
        commentsLiveData = liveData {
            val data = placesRepository.getProductComments(placeId, page, accessToken)
            emit(data)
        }
        return commentsLiveData

    }

    suspend fun getProductDetails(placeId: String, accessToken: String): LiveData<Product?>?{
//        if (productLiveData != null) {
//            return productLiveData
//        }
        //Commented to add the swipe functionality
        productLiveData = liveData {
            val data = placesRepository.getProductDetails(placeId, accessToken)
            emit(data)
        }
        return productLiveData
    }

    suspend fun getFavoriteProducts(accessToken: String): LiveData<List<FavoriteProduct>?>?{
        if (favoritePlacesLiveData != null) {
            return favoritePlacesLiveData
        }
        favoritePlacesLiveData = liveData {
            val data = placesRepository.getFavoriteProducts(accessToken)
            emit(data)
        }
        return favoritePlacesLiveData
    }

    suspend fun getProductRate(placeId: String, accessToken: String): Rate? {
        return if (userSpecificPlaceRate != null){ userSpecificPlaceRate }
        else{
            userSpecificPlaceRate = ratingRepository.getProductRate(placeId, accessToken)
            userSpecificPlaceRate
        }
    }

    suspend fun addCommentOnProduct(
        productComment: ProductComment,
        accessToken: String
    ): ResponseMessage? {
        return commentsRepository.addCommentOnProduct(productComment, accessToken)
    }

    suspend fun updateCommentOnProduct(
        commentId: String,
        productComment: ProductComment,
        accessToken: String
    ): ResponseMessage? {
        return commentsRepository.updateCommentOnProduct(commentId, productComment, accessToken)
    }

    suspend fun deleteCommentFromProduct(commentId: String, accessToken: String): ResponseMessage? {
        return commentsRepository.deleteCommentFromProduct(commentId, accessToken)
    }

    suspend fun addRatingToProduct(rate: Rate, placeId: String, accessToken: String): ResponseMessage?{
        return ratingRepository.addRatingToProduct(rate, placeId, accessToken)
    }

    suspend fun updateRatingToProduct(rate: Rate, placeId: String, accessToken: String): ResponseMessage?{
        return ratingRepository.updateRatingToProduct(rate, placeId, accessToken)
    }

    suspend fun addProductToFavorites(favoriteProduct: VisitedProduct, accessToken: String): ResponseMessage?{
        return placesRepository.addProductToFavorites(favoriteProduct, accessToken)
    }

    suspend fun deleteProductFromFavorites(placeId: String, accessToken: String): ResponseMessage?{
        return placesRepository.deleteProductFromFavorites(placeId, accessToken)
    }
}