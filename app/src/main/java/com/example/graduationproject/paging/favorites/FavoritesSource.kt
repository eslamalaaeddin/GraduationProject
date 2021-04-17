package com.example.graduationproject.paging.favorites

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.repository.ProductsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "FavoritesSource"
class FavoritesSource(
    private val productsRepository: ProductsRepository,
    private val accessToken: String
) : PageKeyedDataSource<Int, FavoriteProduct>() {
    private var dummyFavSource = mutableListOf<FavoriteProduct>()
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, FavoriteProduct>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = productsRepository.getFavoriteProducts(1, accessToken)
            response.let { res ->
                val favoriteProducts = res.orEmpty()
                dummyFavSource.addAll(favoriteProducts)
                callback.onResult(favoriteProducts, null, 2)
                Log.i(TAG, "BBBB loadInitial: $favoriteProducts" )
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, FavoriteProduct>) {


    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, FavoriteProduct>) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = productsRepository.getFavoriteProducts(params.key, accessToken)
            response.let { res ->
                val favoriteProducts = res.orEmpty()
                dummyFavSource.addAll(favoriteProducts)
                callback.onResult(favoriteProducts, params.key + 1)
                Log.i(TAG, "BBB loadInitial: $favoriteProducts" )
            }
        }
    }


}