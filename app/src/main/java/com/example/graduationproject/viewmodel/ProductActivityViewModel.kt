package com.example.graduationproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.comments.ProductComment
import com.example.graduationproject.model.products.*
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.paging.comments.CommentsSource
import com.example.graduationproject.paging.comments.CommentsSourceFactory
import com.example.graduationproject.paging.favorites.FavoritesSource
import com.example.graduationproject.paging.favorites.FavoritesSourceFactory
import com.example.graduationproject.repository.CommentsRepository
import com.example.graduationproject.repository.ProductsRepository
import com.example.graduationproject.repository.RatingRepository

private const val TAG = "ProductActivityViewMode"
class ProductActivityViewModel(
    private val commentsRepository: CommentsRepository,
    private val productsRepository: ProductsRepository,
    private val ratingRepository: RatingRepository
) : ViewModel() {
    var commentsLiveData : LiveData<List<Comment>?>? = null
    var productLiveData : LiveData<Product?>? = null
    var favoritePlacesLiveData: LiveData<MutableList<FavoriteProduct>?>? = null
    var userSpecificPlaceRate: Rate? = null

    var favoritePages = 1
    var commentPages = 1

    suspend fun getProductComments(placeId: String, accessToken: String): LiveData<List<Comment>?>?{

        if (commentPages!=-1){
            commentsLiveData = liveData {
                Log.i(TAG, "ccc BEFORE PAGE: $commentPages")
                val data = productsRepository.getProductComments(placeId, commentPages++, accessToken)
                Log.i(TAG, "ccc AFTER PAGE: $commentPages")
                emit(data)
            }
            return commentsLiveData
        }
        return null

    }


    suspend fun getProductDetails(placeId: String, accessToken: String): LiveData<Product?>?{
        productLiveData = liveData {
            val data = productsRepository.getProductDetails(placeId, accessToken)
            emit(data)
        }
        return productLiveData
    }

    suspend fun getFavoriteProducts( accessToken: String): LiveData<MutableList<FavoriteProduct>?>?{

        if (favoritePages != -1){
            favoritePlacesLiveData = liveData {
                Log.i(TAG, "FFF BeforePage: $favoritePages")
                val data = productsRepository.getFavoriteProducts(favoritePages++ , accessToken)
                Log.i(TAG, "FFF AfterPage: $favoritePages")
                emit(data)
            }
            return favoritePlacesLiveData
        }
        return null

    }

    suspend fun getUserSpecificRate(placeId: String, accessToken: String): Rate? {
        return if (userSpecificPlaceRate != null){ userSpecificPlaceRate }
        else{
            userSpecificPlaceRate = ratingRepository.getUserSpecificRate(placeId, accessToken)
            userSpecificPlaceRate
        }
    }

    suspend fun addCommentOnProduct(
        productComment: ProductComment,
        accessToken: String
    ): ReturnedComment? {
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
        return productsRepository.addProductToFavorites(favoriteProduct, accessToken)
    }

    suspend fun deleteProductFromFavorites(placeId: String, accessToken: String): ResponseMessage?{
        return productsRepository.deleteProductFromFavorites(placeId, accessToken)
    }
}