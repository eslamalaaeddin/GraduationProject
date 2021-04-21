package com.example.graduationproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.graduationproject.models.products.*
import com.example.graduationproject.paging.products.RecommendedProductsSource
import com.example.graduationproject.paging.products.RecommendedProductsSourceFactory
import com.example.graduationproject.repository.ProductsRepository

private const val TAG = "HomeFragmentViewModel"
class HomeFragmentViewModel(private val productsRepository: ProductsRepository): ViewModel() {

    var recommendedProductsSourceLiveData : LiveData<RecommendedProductsSource>? = null
    var recommendedProductsPagedList :  LiveData<PagedList<Product>>? = null

    suspend fun getRecommendedProductsPagedList(accessToken: String) : LiveData<PagedList<Product>>?{
        val recommendedProductsSourceFactory = RecommendedProductsSourceFactory(accessToken, productsRepository)
        recommendedProductsSourceLiveData = recommendedProductsSourceFactory.recommendedProductsSourceLiveData

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true) //no items
            .setInitialLoadSizeHint(10)
            .setPageSize(2)
            .setPrefetchDistance(4)
            .build()

            recommendedProductsPagedList = LivePagedListBuilder<Int, Product>(recommendedProductsSourceFactory, config)
                .build()

        return recommendedProductsPagedList
    }


}