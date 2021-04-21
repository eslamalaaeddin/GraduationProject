package com.example.graduationproject.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.graduationproject.cache.dao.FavoritesDao
import com.example.graduationproject.cache.dao.ProductsDao
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.user.User

@Database(entities = arrayOf(FavoriteProduct::class, Product::class), version = 1, exportSchema = false)

abstract class Database : RoomDatabase() {
    abstract fun getFavoritesDao(): FavoritesDao
    abstract fun getProductsDao(): ProductsDao
   // abstract fun getUsersDao(): UsersDao
}