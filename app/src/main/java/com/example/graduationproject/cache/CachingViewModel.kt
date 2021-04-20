package com.example.graduationproject.cache

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.room.withTransaction
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.user.User

private const val TAG = "CachingViewModel"

class CachingViewModel(private val cachingRepository: CachingRepository) : ViewModel() {
    var favoritePlacesLiveData: LiveData<MutableList<FavoriteProduct>>? = null
    var productsLiveData: LiveData<MutableList<Product>>? = null
    var favoritePages = 0
    var productPages = 0

    //FAVORITES DAO
    suspend fun getFavoriteProductsFromDb(): LiveData<MutableList<FavoriteProduct>>? {
        if (favoritePages != -1) {
            favoritePlacesLiveData = liveData {
                Log.i(TAG, "FFF BeforePage: $favoritePages")
                val data = cachingRepository.getFavoritesFromDb(favoritePages)
                favoritePages += 20
                Log.i(TAG, "FFF AfterPage: $favoritePages")
                emit(data)
            }
            return favoritePlacesLiveData
        }
        return null
    }

    private suspend fun insertIntoFavorites(favoriteProduct: FavoriteProduct) =
        cachingRepository.insertIntoFavorites(favoriteProduct)

    private suspend fun deleteFromFavorites(favoriteProduct: FavoriteProduct) =
        cachingRepository.deleteFromFavorites(favoriteProduct)

    //PRODUCTS DAO
    suspend fun getProductsFromDb(): LiveData<MutableList<Product>>? {
        if (productPages != -1) {
            productsLiveData = liveData {
                Log.i(TAG, "FFF BeforePage: $productPages")
                val data = cachingRepository.getProductsFromDb(productPages++)
                Log.i(TAG, "FFF AfterPage: $productPages")
                if (data.isNotEmpty()) {
                    emit(data)
                }
            }
            return productsLiveData
        }
        return null
    }

    suspend fun getProductFromDb(productId: Long) : LiveData<Product?> = liveData {
        val data = cachingRepository.getProduct(productId)
        emit(data)
    }

    private suspend fun insertIntoProducts(product: Product) =
        cachingRepository.insertIntoProducts(product)

    private suspend fun deleteFromProducts(product: Product) =
        cachingRepository.deleteFromProducts(product)


    suspend fun insertAsTransaction(favoriteProduct: FavoriteProduct, product: Product) {
        try {
            cachingRepository.database.withTransaction {
                insertIntoFavorites(favoriteProduct)
                insertIntoProducts(product)
            }
        } catch (ex: Throwable) {
            Log.e(TAG, ex.localizedMessage.orEmpty())
        }

    }

    suspend fun deleteAsTransaction(favoriteProduct: FavoriteProduct, product: Product) {
        try {
            cachingRepository.database.withTransaction {
                deleteFromFavorites(favoriteProduct)
                deleteFromProducts(product)
            }
        } catch (ex: Throwable) {
            Log.e(TAG, ex.localizedMessage.orEmpty())
        }

    }


    //USERS DAO
//    suspend fun getUser(userId: Long) = liveData {
//        val user = cachingRepository.getUser(userId)
//        emit(user)
//    }

    //suspend fun insertUser(user: User) = cachingRepository.insertUser(user)

//    try
//    {
//
//    }
//    catch (ex: Throwable)
//    {
//        Log.e(TAG, ex.localizedMessage.orEmpty())
//    }

}