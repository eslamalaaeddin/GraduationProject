package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.products.Product

interface RecommendedProductClickListener {
    fun onFavoriteIconClicked(product: Product, productPosition: Int)
}