package com.example.graduationproject.paging.products.recommendedbyproduct

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.repository.ProductsRepository

class RecommendedProductsByProductSourceFactory(
    private val productId: String,
    private val accessToken: String,
    private val productsRepository: ProductsRepository
) : DataSource.Factory<Int, Product>() {
    private lateinit var recommendedProductsByProductSource: RecommendedProductsByProductSource
    var recommendedProductsByProductSourceLiveData: MutableLiveData<RecommendedProductsByProductSource> =
        MutableLiveData()

    override fun create(): DataSource<Int, Product> {
        recommendedProductsByProductSource = RecommendedProductsByProductSource(productId, accessToken, productsRepository)
        recommendedProductsByProductSourceLiveData.postValue(recommendedProductsByProductSource)
        return recommendedProductsByProductSource
    }
}