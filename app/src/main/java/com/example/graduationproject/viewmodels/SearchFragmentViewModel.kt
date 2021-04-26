package com.example.graduationproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.user.User
import com.example.graduationproject.paging.searchedproducts.byname.SearchedProductsByNameSource
import com.example.graduationproject.paging.searchedproducts.byname.SearchedProductsSourceByNameFactory
import com.example.graduationproject.paging.searchedproducts.bytag.SearchedProductsByTagSource
import com.example.graduationproject.paging.searchedproducts.bytag.SearchedProductsByTagSourceFactory
import com.example.graduationproject.paging.users.SearchedUsersSource
import com.example.graduationproject.paging.users.SearchedUsersSourceFactory
import com.example.graduationproject.repository.SearchRepository

class SearchFragmentViewModel(private val searchRepository: SearchRepository) : ViewModel() {

    var searchedProductsByNameSourceLiveData : LiveData<SearchedProductsByNameSource>? = null
    var searchedProductsByTagSourceLiveData : LiveData<SearchedProductsByTagSource>? = null
    var searchedProductsPagedList :  LiveData<PagedList<Product>>? = null

    var searchedUsersSourceLiveData : LiveData<SearchedUsersSource>? = null
    var searchedUsersPagedList :  LiveData<PagedList<User>>? = null

     fun getProductsPagedListByName(productName: String, accessToken: String) : LiveData<PagedList<Product>>?{
        val searchedProductsSourceFactory = SearchedProductsSourceByNameFactory(accessToken, searchRepository, productName)
        searchedProductsByNameSourceLiveData = searchedProductsSourceFactory.productsByNameSourceLiveData

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true) //no items
            .setInitialLoadSizeHint(10)
            .setPageSize(2)
            .setPrefetchDistance(4)
            .build()

        searchedProductsPagedList = LivePagedListBuilder<Int, Product>(searchedProductsSourceFactory, config)
            .build()

        return searchedProductsPagedList
    }

    fun getProductsPagedListByTag(tag: String, accessToken: String) : LiveData<PagedList<Product>>?{
        val searchedProductsSourceFactory = SearchedProductsByTagSourceFactory(accessToken, searchRepository, tag)
        searchedProductsByTagSourceLiveData = searchedProductsSourceFactory.productsByTagSourceLiveData

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true) //no items
            .setInitialLoadSizeHint(10)
            .setPageSize(2)
            .setPrefetchDistance(4)
            .build()

        searchedProductsPagedList = LivePagedListBuilder<Int, Product>(searchedProductsSourceFactory, config)
            .build()

        return searchedProductsPagedList
    }


    fun getUsersPagedListByName(userName: String, accessToken: String) : LiveData<PagedList<User>>?{
        val searchedUsersSourceFactory = SearchedUsersSourceFactory(accessToken, searchRepository, userName)
        searchedUsersSourceLiveData = searchedUsersSourceFactory.usersSourceLiveData

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(true) //no items
            .setInitialLoadSizeHint(10)
            .setPageSize(2)
            .setPrefetchDistance(4)
            .build()

        searchedUsersPagedList = LivePagedListBuilder<Int, User>(searchedUsersSourceFactory, config)
            .build()

        return searchedUsersPagedList
    }



}