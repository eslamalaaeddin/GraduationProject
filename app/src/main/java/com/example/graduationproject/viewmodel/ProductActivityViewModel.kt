package com.example.graduationproject.viewmodel

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
import com.example.graduationproject.repository.CommentsRepository
import com.example.graduationproject.repository.ProductsRepository
import com.example.graduationproject.repository.RatingRepository

class ProductActivityViewModel(
    private val commentsRepository: CommentsRepository,
    private val productsRepository: ProductsRepository,
    private val ratingRepository: RatingRepository
) : ViewModel() {
    var commentsLiveData : LiveData<List<Comment>?>? = null
    var productImagesLiveData: LiveData<List<ProductImage>?>? = null
    var productLiveData : LiveData<Product?>? = null
    var favoritePlacesLiveData: LiveData<MutableList<FavoriteProduct>?>? = null
    var userSpecificPlaceRate: Rate? = null

    var commentsSourceLiveData : LiveData<CommentsSource>? = null
    var commentsPagedList :  LiveData<PagedList<Comment>>? = null

//    suspend fun getProductComments(placeId: String, page: Int, accessToken: String): LiveData<List<Comment>?>?{
//        // i hide it as i want comment to be real time
////        if (commentsLiveData != null) {
////            return commentsLiveData
////        }
//
//        commentsLiveData = liveData {
//            val data = productsRepository.getProductComments(placeId, page, accessToken)
//            emit(data)
//        }
//        return commentsLiveData
//
//
//    }

    suspend fun getCommentsPagedList(productId: String, accessToken: String) : LiveData<PagedList<Comment>>?{
        val commentsSourceFactory = CommentsSourceFactory(productsRepository, productId, accessToken)
        commentsSourceLiveData = commentsSourceFactory.commentsSourceLiveData
        //Making configs

        //Making configs
        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true) //no items
            .setInitialLoadSizeHint(3)
            .setPageSize(2)
            .setPrefetchDistance(1)
            .build()

//        val executor: Executor = Executors.newFixedThreadPool(5)
        //building the paged list
        //building the paged list
        commentsPagedList = LivePagedListBuilder<Int, Comment>(commentsSourceFactory, config)
//            .setFetchExecutor(executor)
            .build()

        return commentsPagedList
    }

    suspend fun getProductDetails(placeId: String, accessToken: String): LiveData<Product?>?{
//        if (productLiveData != null) {
//            return productLiveData
//        }
        //Commented to add the swipe functionality
        productLiveData = liveData {
            val data = productsRepository.getProductDetails(placeId, accessToken)
            emit(data)
        }
        return productLiveData
    }

    suspend fun getFavoriteProducts(accessToken: String): LiveData<MutableList<FavoriteProduct>?>?{
        if (favoritePlacesLiveData != null) {
            return favoritePlacesLiveData
        }
        favoritePlacesLiveData = liveData {
            val data = productsRepository.getFavoriteProducts(accessToken)
            emit(data)
        }
        return favoritePlacesLiveData
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
        return productsRepository.addProductToFavorites(favoriteProduct, accessToken)
    }

    suspend fun deleteProductFromFavorites(placeId: String, accessToken: String): ResponseMessage?{
        return productsRepository.deleteProductFromFavorites(placeId, accessToken)
    }
}