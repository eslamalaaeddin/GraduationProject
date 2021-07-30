package com.example.graduationproject.models.comments

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

//Base comment model that contains only the text comment -- Used For POST and PUT requests.
//There is another Comment model that is used to get the whole comment (text, user image, user name ....)
data class ProductComment(
    @SerializedName("pid")
    var productId: Long? = null,
    var comment: String? = null
)
