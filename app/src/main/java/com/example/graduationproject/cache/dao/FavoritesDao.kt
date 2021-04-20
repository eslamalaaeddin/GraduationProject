package com.example.graduationproject.cache.dao

import androidx.room.*
import com.example.graduationproject.models.products.FavoriteProduct

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorite_products LIMIT 20 OFFSET :page")
    suspend fun getFavoritesFromDb(page: Int) : MutableList<FavoriteProduct>
    //page 0, 10, 20, 30, 40.... so it will start from zero

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoFavorites(favoriteProduct: FavoriteProduct)

    @Delete
    suspend fun deleteFromFavorites(favoriteProduct: FavoriteProduct)
}