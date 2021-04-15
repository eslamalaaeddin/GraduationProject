package com.example.graduationproject.model.products

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

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
){

    fun convertServerTimeToTimestamp(serverTime: String): Long{
        val dateOnly = serverTime.substring(5, 25)
        val formatter: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
        val date: Date = formatter.parse(dateOnly) as Date
        return date.time / 1000
    }


}