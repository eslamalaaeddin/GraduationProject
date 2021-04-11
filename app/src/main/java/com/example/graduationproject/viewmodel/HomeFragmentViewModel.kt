package com.example.graduationproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.graduationproject.model.products.*
import com.example.graduationproject.paging.products.RecommendedProductsSource
import com.example.graduationproject.paging.products.RecommendedProductsSourceFactory
import com.example.graduationproject.repository.ProductsRepository

private const val TAG = "HomeFragmentViewModel"
class HomeFragmentViewModel(private val productsRepository: ProductsRepository): ViewModel() {
    var recommendedPlacesLiveData:LiveData<List<Product>?>? = null

    var recommendedProductsSourceLiveData : LiveData<RecommendedProductsSource>? = null
    var recommendedProductsPagedList :  LiveData<PagedList<Product>>? = null

//    suspend fun addNewPlace(product: Product, accessToken: String): ResponseMessage?{
//        return productsRepository.addNewPlace(product, accessToken)
//    }


//    suspend fun getRecommendedProducts(page: Int, accessToken: String): LiveData<List<Product>?>?{
////        if (recommendedPlacesLiveData != null){
////            return recommendedPlacesLiveData
////        }
//        recommendedPlacesLiveData = liveData {
//            val data = productsRepository.getRecommendedPlaces(page,accessToken)
//            emit(data)
//        }
//        return recommendedPlacesLiveData
//    }


    suspend fun getRecommendedProductsPagedList(accessToken: String) : LiveData<PagedList<Product>>?{
        val recommendedProductsSourceFactory = RecommendedProductsSourceFactory(accessToken, productsRepository)
        recommendedProductsSourceLiveData = recommendedProductsSourceFactory.recommendedProductsSourceLiveData
        //Making configs

        //Making configs
        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true) //no items
            .setInitialLoadSizeHint(10)
            .setPageSize(2)
            .setPrefetchDistance(4)
            .build()

//        val executor: Executor = Executors.newFixedThreadPool(5)
        //building the paged list
        //building the paged list
            recommendedProductsPagedList = LivePagedListBuilder<Int, Product>(recommendedProductsSourceFactory, config)
//            .setFetchExecutor(executor)
                .build()

        return recommendedProductsPagedList
    }


}