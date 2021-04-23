package com.example.graduationproject.models.products

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    var id: Long? = null,
    var image: String? = null,
    @SerializedName("is_favorite")
    var favorite: Int? = null,
    var name: String? = null,
    var rating: Float? = null,
    var description: String? = null,
    var tags: String? = null,
    @ColumnInfo(name = "user_rate")
    var userRate: Float? = null
)
{
    override fun toString(): String {
        return name.orEmpty()
    }
}