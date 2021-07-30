package com.example.graduationproject.paging.products.recommendedbyproduct

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.repository.ProductsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max

private const val TAG = "RecommendedProductsSour"

class RecommendedProductsByProductSource(
    private val productId: String,
    private val accessToken: String,
    private val productsRepository: ProductsRepository
) : PageKeyedDataSource<Int, Product>() {
    var maxPage = 0

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Product>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = productsRepository.getRecommendedProductsByProduct(productId, 1, accessToken)
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
            val response = productsRepository.getRecommendedProductsByProduct(productId, params.key, accessToken)
            response.let { res ->
                val products = res.orEmpty()
                maxPage++
                if (maxPage <= 2){
                    callback.onResult(products, params.key + 1)
                }
                else{
                    callback.onResult(products, null)
                }
                Log.i(TAG, "IIII loadInitial: $products")
            }
        }
    }
}