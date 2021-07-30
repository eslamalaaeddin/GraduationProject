package com.example.graduationproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.graduationproject.cache.CachingRepository
import com.example.graduationproject.helper.BaseApplication
import com.example.graduationproject.models.products.*
import com.example.graduationproject.paging.products.recommended.RecommendedProductsSource
import com.example.graduationproject.paging.products.recommended.RecommendedProductsSourceFactory
import com.example.graduationproject.repository.ProductsRepository

private const val TAG = "HomeFragmentViewModel"

class HomeFragmentViewModel(
    private val productsRepository: ProductsRepository,
    private val cachingRepository: CachingRepository
) : ViewModel() {

    var recommendedProductsSourceLiveData: LiveData<RecommendedProductsSource>? = null
    var recommendedProductsPagedList: LiveData<PagedList<Product>>? = null

    suspend fun getRecommendedProductsPagedList(accessToken: String): LiveData<PagedList<Product>>? {
        //Constructing the Paging source
        //getConnectionType() has two variants one with context parameter and the other without it
        //as i should not pass any contexts to view models as it may cause memory leaks.
        val recommendedProductsSourceFactory =
            RecommendedProductsSourceFactory(accessToken,
                    productsRepository, cachingRepository, networkState = BaseApplication.getConnectionType())

        //This makes the product source used as live data.
        recommendedProductsSourceLiveData =
            recommendedProductsSourceFactory.recommendedProductsSourceLiveData

        //Configures how a PagedList loads content from its PagingSource.
        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(10)//Number of items loaded initially.
            .setPageSize(2)//Number of items loaded at a time.
            .setPrefetchDistance(4)
            .build()

        //<Key, Value> --> Key is the page number, Value is the base type needed.
        //It creates a live data of the paged list --> LiveData<PagedList<Product>>
        recommendedProductsPagedList =
            LivePagedListBuilder<Int, Product>(recommendedProductsSourceFactory, config)
                .build()

        return recommendedProductsPagedList
    }



}