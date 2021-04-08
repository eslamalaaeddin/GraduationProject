package com.example.graduationproject.paging.products

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.graduationproject.model.products.Product
import com.example.graduationproject.repository.ProductsRepository

class RecommendedProductsSourceFactory(
    private val accessToken: String,
    private val productsRepository: ProductsRepository
) : DataSource.Factory<Int, Product>() {
    private lateinit var recommendedProductsSource: RecommendedProductsSource
    var recommendedProductsSourceLiveData: MutableLiveData<RecommendedProductsSource> =
        MutableLiveData()

    override fun create(): DataSource<Int, Product> {
        recommendedProductsSource = RecommendedProductsSource(accessToken, productsRepository)
        recommendedProductsSourceLiveData.postValue(recommendedProductsSource)
        return recommendedProductsSource
    }
}