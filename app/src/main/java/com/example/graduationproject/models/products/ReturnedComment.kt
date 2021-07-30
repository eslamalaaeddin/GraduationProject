package com.example.graduationproject.models.products

import com.google.gson.annotations.SerializedName

//Base comment mode -- Used For GET request when a user makes one.
data class ReturnedComment(
    @SerializedName("cid")
    var commentId: Long? = null,
    var time: String? = null
)