package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.products.FavoriteProduct

interface FavoriteProductClickListener {
   fun onFavoriteIconClicked(favoriteProduct: FavoriteProduct?, productPosition: Int)
}