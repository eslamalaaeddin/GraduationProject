package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.products.Product

interface RecommendedPlaceClickListener {
    fun onFavoriteIconClicked(product: Product)
}