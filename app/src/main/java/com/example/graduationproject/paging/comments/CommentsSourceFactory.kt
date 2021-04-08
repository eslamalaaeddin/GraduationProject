package com.example.graduationproject.paging.comments

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.graduationproject.model.products.Comment
import com.example.graduationproject.repository.ProductsRepository


class CommentsSourceFactory(
    private val productsRepository: ProductsRepository,
    private val productId: String,
    private val accessToken: String
) : DataSource.Factory<Int, Comment>() {

    private lateinit var commentsSource: CommentsSource
    var commentsSourceLiveData: MutableLiveData<CommentsSource> = MutableLiveData()


    override fun create(): DataSource<Int, Comment> {
        commentsSource = CommentsSource(productsRepository, productId, accessToken)
        commentsSourceLiveData.postValue(commentsSource)
        return commentsSource
    }


}