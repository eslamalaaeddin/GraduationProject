package com.example.graduationproject.models.products

import com.google.gson.annotations.SerializedName
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class Comment(
    @SerializedName("comment_id")
    var commentId: Long? = null,
    var rate: Float? = null,
    @SerializedName("text")
    var commentContent: String? = null,
    var time: String? = null,
    @SerializedName("user_id")
    var userId: Long? = null,
    @SerializedName("user_image")
    var userImage: String? = null,
    @SerializedName("username")
    var userName: String? = null,
    val timeStamp : Long = System.currentTimeMillis()
)