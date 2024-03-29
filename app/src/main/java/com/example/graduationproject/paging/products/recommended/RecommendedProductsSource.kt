package com.example.graduationproject.paging.products.recommended

import android.util.Log
import androidx.paging.PageKeyedDataSource
import androidx.room.withTransaction
import com.example.graduationproject.cache.CachingRepository
import com.example.graduationproject.helper.BaseApplication
import com.example.graduationproject.helper.Utils
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.repository.ProductsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "RecommendedProductsSour"

class RecommendedProductsSource(
    private val accessToken: String,
    private val productsRepository: ProductsRepository,
    private val cachingRepository: CachingRepository,
    private val networkState: Int
) : PageKeyedDataSource<Int, Product>() {
    private var offlineOffset = 0
    private var onlineOffset = 0

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Product>
    ) {
        //ONLINE
        if (networkState != 0) {
            CoroutineScope(Dispatchers.Main).launch {
                val response = productsRepository.getRecommendedProducts(1, accessToken)
                response.let { res ->
                    val products = res.orEmpty().onEach { it.favorite = 0 }
                    cachingRepository.database.withTransaction {
                        cachingRepository.deleteProductsFromDb()
                        delay(1000)
                        cachingRepository.insertProducts(products)
                    }
                    callback.onResult(products.distinct(), null, 2)
                }
            }
        }

        //OFFLINE
        else if (networkState == 0) {
            CoroutineScope(Dispatchers.Main).launch {
                val response = cachingRepository.getProductsFromDb(offlineOffset)
                offlineOffset += 10
                response.let { res ->
                    val products = res.orEmpty()
                    callback.onResult(products, null, 2)
                    Log.i(TAG, "IIII LOCAL loadInitial: ${products.size}")
                }
            }
        }

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Product>) {

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Product>) {
        if (networkState != 0) {
            CoroutineScope(Dispatchers.Main).launch {
                val response = productsRepository.getRecommendedProducts(params.key, accessToken)
                response.let { res ->
                    val products = res.orEmpty().onEach { it.favorite = 0 }
                    Log.i(TAG, "XXXXXX loadAfter: $products")
                    cachingRepository.database.withTransaction {
                        cachingRepository.insertProducts(products)
                    }
                    callback.onResult(products.distinct(), params.key + 1)
                }
            }
        } else if (networkState == 0) {
            CoroutineScope(Dispatchers.Main).launch {
                val response = cachingRepository.getProductsFromDb(offlineOffset)
                offlineOffset += 10
                response.let { res ->
                    val products = res.orEmpty()
                    callback.onResult(products, params.key + 1)
                }
            }

        }

    }

}