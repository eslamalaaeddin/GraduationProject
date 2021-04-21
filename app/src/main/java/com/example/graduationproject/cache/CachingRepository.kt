package com.example.graduationproject.cache

import android.content.Context
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.user.User
import com.example.graduationproject.repository.BaseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CachingRepository(
    val database: Database,
    val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(context) {

    //FAVORITES DAO
    suspend fun getFavoritesFromDb(page: Int) = database.getFavoritesDao().getFavoritesFromDb(page)
    suspend fun insertIntoFavorites(favoriteProduct: FavoriteProduct) = database.getFavoritesDao().insertIntoFavorites(favoriteProduct)
    suspend fun deleteFromFavorites(favoriteProduct: FavoriteProduct) = database.getFavoritesDao().deleteFromFavorites(favoriteProduct)

    //PRODUCTS DAO
    suspend fun getProductsFromDb(page: Int) = database.getProductsDao().getProductsFromDb(page)
    suspend fun getProduct(productId: Long) = database.getProductsDao().getProduct(productId)
    suspend fun insertIntoProducts(product: Product) = database.getProductsDao().insertIntoProducts(product)
    suspend fun deleteFromProducts(product: Product) = database.getProductsDao().deleteFromProducts(product)

    //USERS DAO
//    suspend fun getUser(userId: Long) = database.getUsersDao().getUser(userId)
//    suspend fun insertUser(user: User) = database.getUsersDao().insertUser(user)


}