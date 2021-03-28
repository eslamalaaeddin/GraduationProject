package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.products.FavoriteProduct

interface FavoritePlaceClickListener {
   fun onFavoriteIconClicked(favoriteProduct: FavoriteProduct)
}