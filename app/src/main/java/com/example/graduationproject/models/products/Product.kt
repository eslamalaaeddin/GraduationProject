package com.example.graduationproject.models.products

import com.google.gson.annotations.SerializedName


data class Product(
    var id: Long? = null,
    var image: String? = null,
    @SerializedName("is_favorite")
    var isFavorite: Int? = null,
    var name: String? = null,
    var rating: Float? = null,
    var description: String? = null,
    var tags: String? = null
)