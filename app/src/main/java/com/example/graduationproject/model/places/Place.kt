package com.example.graduationproject.model.places

import com.google.gson.annotations.SerializedName

/*
 "id": 3,
    "image": "img31.png",
    "is_favorite": 0,
    "name": "p3",
    "rating": 4.333333333

   "city": "c1",
  "country": "cn1",
  "lat": 0,
  "lng": 0,
 */
data class Place(
    var id: String? = null,
    //I don't know how to show this images as it is does not provide any links
    var image: String? = null,
    @SerializedName("is_favorite")
    var isFavorite: Int? = null,
    var name: String? = null,
    var rating: Double? = null,
    var city: String? = null,
    var country: String? = null,
    @SerializedName("lat")
    var latitude: Double? = null,
    @SerializedName("lng")
    var longitude: Double? = null
)