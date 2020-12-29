package com.example.graduationproject.model.comments

import com.google.gson.annotations.SerializedName

data class PlaceComment(
    @SerializedName("pid")
    var placeId: Int? = null,
    var comment: String? = null,
    var time: String? = "2020-09-25 20:13:00"
)