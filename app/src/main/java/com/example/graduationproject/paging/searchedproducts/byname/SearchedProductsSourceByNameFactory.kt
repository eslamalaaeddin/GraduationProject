package com.example.graduationproject.paging.searchedproducts.byname

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.graduationproject.model.products.Product
import com.example.graduationproject.repository.SearchRepository

class SearchedProductsSourceByNameFactory(
    private val accessToken: String,
    private val searchRepository: SearchRepository,
    private val productName: String
) : DataSource.Factory<Int, Product>() {
    private lateinit var productsByNameSource: SearchedProductsByNameSource
    var productsByNameSourceLiveData: MutableLiveData<SearchedProductsByNameSource> =
        MutableLiveData()

    override fun create(): DataSource<Int, Product> {
        productsByNameSource = SearchedProductsByNameSource(productName, accessToken, searchRepository)
        productsByNameSourceLiveData.postValue(productsByNameSource)
        return productsByNameSource
    }
}