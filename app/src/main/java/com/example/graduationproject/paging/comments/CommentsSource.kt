package com.example.graduationproject.paging.comments

import android.content.Context
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.graduationproject.helper.ExceptionHandler
import com.example.graduationproject.model.products.Comment
import com.example.graduationproject.network.Api
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.repository.ProductsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception

private const val TAG = "CommentsSource"
//I should have provide a repo as a parameter
class CommentsSource(
    private val productsRepository: ProductsRepository,
    private val productId: String,
    private val accessToken: String
    ) : PageKeyedDataSource<Int, Comment>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Comment>
    ) {
            //I don't know why not to use MAIN dispatcher here
            CoroutineScope(Dispatchers.Main).launch {
                val response = productsRepository.getProductComments(productId, 1, accessToken)
                response.let { res ->
                    val comments = res.orEmpty()
                    callback.onResult(comments, null, 2)
                    Log.i(TAG, "IIII loadInitial: $comments" )
                }
            }

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Comment>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Comment>) {
            CoroutineScope(Dispatchers.Main).launch {
                val response = productsRepository.getProductComments(productId, params.key, accessToken)
                response.let { res ->
                    val comments = res.orEmpty()
                    callback.onResult(comments, params.key + 1)
                    Log.i(TAG, "IIII loadInitial: $comments" )
                }
            }

    }

}