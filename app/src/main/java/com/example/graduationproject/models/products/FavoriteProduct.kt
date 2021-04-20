package com.example.graduationproject.models.products

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_products")
data class FavoriteProduct(
    @PrimaryKey
    var id: Long? = null,
    var image: String? = null,
    var name: String? = null,
    var rating: Float? = null,
    var favorite : Int = 1,
    @Ignore var isFavorite : Boolean = true

){
    override fun toString(): String {
        return "$name FAVORITE PRODUCT $id"
    }
}