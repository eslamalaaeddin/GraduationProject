package com.example.graduationproject.model.comments

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class PlaceComment(
    @SerializedName("pid")
    var placeId: Long? = null,
    var comment: String? = null
)

fun getTime(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
//    val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
//    val currentDateAndTime: String = simpleDateFormat.format(Date())
//}