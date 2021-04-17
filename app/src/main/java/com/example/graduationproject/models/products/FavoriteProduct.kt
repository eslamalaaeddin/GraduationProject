package com.example.graduationproject.models.products

data class FavoriteProduct(
    var id: Long? = null,
    var image: String? = null,
    var name: String? = null,
    var rating: Float? = null,
    var isFavorite : Boolean = true
)