package com.example.graduationproject.cache.dao

import androidx.room.*
import com.example.graduationproject.models.products.Product

@Dao
interface ProductsDao {

    //same as favorite so it is commented
    @Query("SELECT * FROM products LIMIT 20 OFFSET :page")
    suspend fun getProductsFromDb(page: Int) : MutableList<Product>
    //page 0, 10, 20, 30, 40.... so it will start from zero

    @Query("SELECT * FROM products WHERE products.id = :productId LIMIT 1")
    suspend fun getProduct(productId: Long) : Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoProducts(product: Product)

    @Delete
    suspend fun deleteFromProducts(product: Product)
}