package com.example.graduationproject.model.places

import com.google.gson.annotations.SerializedName

/*
     "comment_id": 1,
    "rate": 2,
    "text": "c1 p1 u1",
    "time": "Wed, 01 Jan 2020 01:10:12 GMT",
    "user_id": 1,
    "user_image": "u1.png",
    "username": "u1 u1"
 */
data class Comment(
    @SerializedName("comment_id")
    var commentId: Int? = null,
    var rate: String? = null,
    @SerializedName("text")
    var commentContent: String? = null,
    var time: String? = null,
    @SerializedName("user_id")
    var userId: Int? = null,
    @SerializedName("user_image")
    var userImage: String? = null,
    @SerializedName("username")
    var userName: String? = null
)