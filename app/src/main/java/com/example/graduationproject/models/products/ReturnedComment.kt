package com.example.graduationproject.models.products

import com.google.gson.annotations.SerializedName

data class ReturnedComment(
    @SerializedName("cid")
    var commentId: Long? = null,
    var time: String? = null
)