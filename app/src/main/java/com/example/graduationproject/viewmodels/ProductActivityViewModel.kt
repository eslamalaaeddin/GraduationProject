package com.example.graduationproject.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.comments.ProductComment
import com.example.graduationproject.models.products.*
import com.example.graduationproject.models.rating.Rate
import com.example.graduationproject.paging.products.recommendedbyproduct.RecommendedProductsByProductSource
import com.example.graduationproject.paging.products.recommendedbyproduct.RecommendedProductsByProductSourceFactory
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

    var recommendedProductsByProductSourceLiveData : LiveData<RecommendedProductsByProductSource>? = null
    var recommendedProductsByProductPagedList :  LiveData<PagedList<Product>>? = null

    suspend fun getRecommendedProductsByProductPagedList(productId: String, accessToken: String) : LiveData<PagedList<Product>>?{
        val recommendedProductsByProductSourceFactory = RecommendedProductsByProductSourceFactory(productId, accessToken, productsRepository)
        recommendedProductsByProductSourceLiveData = recommendedProductsByProductSourceFactory.recommendedProductsByProductSourceLiveData

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true) //no items
            .setInitialLoadSizeHint(10)
            .setPageSize(2)
            .setPrefetchDistance(4)
            .build()

        recommendedProductsByProductPagedList = LivePagedListBuilder<Int, Product>(recommendedProductsByProductSourceFactory, config)
            .build()

        return recommendedProductsByProductPagedList
    }

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