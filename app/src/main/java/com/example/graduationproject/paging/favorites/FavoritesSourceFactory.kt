package com.example.graduationproject.paging.favorites

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.graduationproject.model.products.Comment
import com.example.graduationproject.model.products.FavoriteProduct
import com.example.graduationproject.paging.comments.CommentsSource
import com.example.graduationproject.repository.ProductsRepository

class FavoritesSourceFactory(
    private val productsRepository: ProductsRepository,
    private val accessToken: String
) : DataSource.Factory<Int, FavoriteProduct>() {

    private lateinit var favoritesSource: FavoritesSource
    var favoritesSourceLiveData: MutableLiveData<FavoritesSource> = MutableLiveData()

    override fun create(): DataSource<Int, FavoriteProduct> {
        favoritesSource = FavoritesSource(productsRepository, accessToken)
        favoritesSourceLiveData.postValue(favoritesSource)
        return favoritesSource
    }
}