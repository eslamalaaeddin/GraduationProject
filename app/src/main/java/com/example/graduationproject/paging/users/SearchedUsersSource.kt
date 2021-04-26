package com.example.graduationproject.paging.users

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.user.User
import com.example.graduationproject.repository.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "RecommendedProductsSour"

class SearchedUsersSource(
    private val productName: String,
    private val accessToken: String,
    private val searchRepository: SearchRepository
) : PageKeyedDataSource<Int, User>() {


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, User>
    ) {
        //I don't know why not to use MAIN dispatcher here
        CoroutineScope(Dispatchers.Main).launch {
//            val response = searchRepository.searchByProductName(productName, 1, accessToken)
//            response.let { res ->
//                val products = res.orEmpty()
//                callback.onResult(products, null, 2)
//                Log.i(TAG, "IIII loadInitial: $products")
//            }
        }


    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        CoroutineScope(Dispatchers.Main).launch {
//            val response = searchRepository.searchByProductName(productName, params.key, accessToken)
//            response.let { res ->
//                val products = res.orEmpty()
//                callback.onResult(products, params.key + 1)
//                Log.i(TAG, "IIII loadInitial: $products")
//            }
        }
    }
}