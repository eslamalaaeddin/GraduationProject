package com.example.graduationproject.helpers.listeners

import com.example.graduationproject.models.products.FavoriteProduct

interface FavoriteProductClickListener {
   fun onFavoriteIconClicked(favoriteProduct: FavoriteProduct?, productPosition: Int)
   fun onFavoriteProductClicked(favoriteProduct: FavoriteProduct?, position: Int)
}