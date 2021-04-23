package com.example.graduationproject.cache

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.models.products.Product
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
    suspend fun insertIntoFavorites(favoriteProduct: FavoriteProduct) = database.getFavoritesDao().insertIntoFavorites(
        favoriteProduct
    )
    suspend fun deleteFromFavorites(favoriteProduct: FavoriteProduct) = database.getFavoritesDao().deleteFromFavorites(
        favoriteProduct
    )

    //PRODUCTS DAO
//    suspend fun getProductsFromDb() = database.getProductsDao().getProductsFromDb()
    suspend fun getProduct(productId: Long) = database.getProductsDao().getProduct(productId)
    suspend fun insertIntoProducts(product: Product) = database.getProductsDao().insertIntoProducts(
        product
    )
    suspend fun deleteFromProducts(product: Product) = database.getProductsDao().deleteFromProducts(
        product
    )


    //CACHING HOME

    suspend fun getProductsFromDb(offset: Int): MutableList<Product>? = database.getProductsDao().getProductsFromDb(offset)
    suspend fun deleteProductsFromDb() = database.getProductsDao().deleteProducts()
    suspend fun insertProducts(products: List<Product>) = database.getProductsDao().insertProducts(products)



}