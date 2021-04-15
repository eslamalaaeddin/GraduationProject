package com.example.graduationproject.paging.searchedproducts.bytag

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.graduationproject.model.products.Product
import com.example.graduationproject.repository.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "RecommendedProductsSour"

class SearchedProductsByTagSource(
    private val tag: String,
    private val accessToken: String,
    private val searchRepository: SearchRepository
) : PageKeyedDataSource<Int, Product>() {


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Product>
    ) {
        //I don't know why not to use MAIN dispatcher here
        CoroutineScope(Dispatchers.Main).launch {
            val response = searchRepository.searchByProductTag(tag, 1, accessToken)
            response.let { res ->
                val products = res.orEmpty()
                callback.onResult(products, null, 2)
                Log.i(TAG, "IIII loadInitial: $products")
            }
        }


    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Product>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Product>) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = searchRepository.searchByProductTag(tag, params.key, accessToken)
            response.let { res ->
                val products = res.orEmpty()
                callback.onResult(products, params.key + 1)
                Log.i(TAG, "IIII loadInitial: $products")
            }
        }
    }
}