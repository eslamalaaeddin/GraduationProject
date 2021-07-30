package com.example.graduationproject.paging.products.recommended

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.graduationproject.cache.CachingRepository
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.repository.ProductsRepository

/**
 * Data-loading systems of an application or library can implement this interface 
 * to allow LiveData<PagedList>s to be created.
 */
class RecommendedProductsSourceFactory(
    private val accessToken: String = "",
    private val productsRepository: ProductsRepository,
    private val cachingRepository: CachingRepository,
    private val networkState: Int
) : DataSource.Factory<Int, Product>() {
    private lateinit var recommendedProductsSource: RecommendedProductsSource
    var recommendedProductsSourceLiveData: MutableLiveData<RecommendedProductsSource> =
        MutableLiveData()

    override fun create(): DataSource<Int, Product> {
        recommendedProductsSource = RecommendedProductsSource(accessToken, productsRepository, cachingRepository, networkState)
        recommendedProductsSourceLiveData.postValue(recommendedProductsSource)
        return recommendedProductsSource
    }
}