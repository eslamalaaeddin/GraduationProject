package com.example.graduationproject.helpers.listeners

import com.example.graduationproject.models.products.Product

interface RecommendedProductClickListener {
    fun onFavoriteIconClicked(product: Product, productPosition: Int)

}