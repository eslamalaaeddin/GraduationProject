package com.example.graduationproject.paging.products

import android.content.Context
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.graduationproject.helper.ExceptionHandler
import com.example.graduationproject.model.products.Product
import com.example.graduationproject.network.Api
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.repository.ProductsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

private const val TAG = "RecommendedProductsSour"

class RecommendedProductsSource(
    private val accessToken: String,
    private val productsRepository: ProductsRepository
) : PageKeyedDataSource<Int, Product>() {


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Product>
    ) {
        //I don't know why not to use MAIN dispatcher here
        CoroutineScope(Dispatchers.Main).launch {
            val response = productsRepository.getRecommendedPlaces(1, accessToken)
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
            val response = productsRepository.getRecommendedPlaces(params.key, accessToken)
            response.let { res ->
                val products = res.orEmpty()
                callback.onResult(products, params.key + 1)
                Log.i(TAG, "IIII loadInitial: $products")
            }
        }
    }
}