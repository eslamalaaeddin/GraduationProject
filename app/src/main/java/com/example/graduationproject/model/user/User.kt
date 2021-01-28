package com.example.graduationproject.model.user

import com.google.gson.annotations.SerializedName

data class User(
    var email: String? = null,
    @SerializedName("firstname") var firstName: String? = null,
    var id: Long? = null,
    var image: String? = null,
    @SerializedName("lastname") var lastName: String? = null
)