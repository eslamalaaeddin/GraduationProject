package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.places.FavoritePlace

interface FavoritePlaceClickListener {
   fun onFavoriteIconClicked(favoritePlace: FavoritePlace)
}