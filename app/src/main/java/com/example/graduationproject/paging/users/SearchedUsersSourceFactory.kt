package com.example.graduationproject.paging.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.user.User
import com.example.graduationproject.repository.SearchRepository

class SearchedUsersSourceFactory(
    private val accessToken: String,
    private val searchRepository: SearchRepository,
    private val userName: String
) : DataSource.Factory<Int, User>() {
    private lateinit var searchedUsersSource: SearchedUsersSource
    var usersSourceLiveData: MutableLiveData<SearchedUsersSource> =
        MutableLiveData()

    override fun create(): DataSource<Int, User> {
        searchedUsersSource = SearchedUsersSource(userName, accessToken, searchRepository)
        usersSourceLiveData.postValue(searchedUsersSource)
        return searchedUsersSource
    }
}