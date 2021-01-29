package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.places.Place

interface RecommendedPlaceClickListener {
    fun onFavoriteIconClicked(place: Place)
}