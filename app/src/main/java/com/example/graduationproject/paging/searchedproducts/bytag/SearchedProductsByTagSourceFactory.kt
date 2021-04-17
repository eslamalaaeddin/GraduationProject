package com.example.graduationproject.paging.searchedproducts.bytag

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.repository.SearchRepository

class SearchedProductsByTagSourceFactory(
    private val accessToken: String,
    private val searchRepository: SearchRepository,
    private val tag: String
) : DataSource.Factory<Int, Product>() {
    private lateinit var productsByTagSource: SearchedProductsByTagSource
    var productsByTagSourceLiveData: MutableLiveData<SearchedProductsByTagSource> =
        MutableLiveData()

    override fun create(): DataSource<Int, Product> {
        productsByTagSource = SearchedProductsByTagSource(tag, accessToken, searchRepository)
        productsByTagSourceLiveData.postValue(productsByTagSource)
        return productsByTagSource
    }
}