package com.example.graduationproject.cache.dao

import androidx.room.*
import com.example.graduationproject.models.products.Product

@Dao
interface ProductsDao {

    @Query("SELECT * FROM products WHERE products.id = :productId LIMIT 1")
    suspend fun getProduct(productId: Long) : Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoProducts(product: Product)

    @Delete
    suspend fun deleteFromProducts(product: Product)

    @Query("SELECT * FROM products LIMIT 10 OFFSET :offset")
    suspend fun getProductsFromDb(offset: Int) : MutableList<Product>?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProducts(cachedProducts: List<Product>)

    @Query("DELETE FROM products WHERE favorite = 0 ")
    suspend fun deleteProducts()
}