package com.example.graduationproject.model.user

import com.google.gson.annotations.SerializedName

data class UserName(
    @SerializedName("firstname") var firstName: String? = null,
    @SerializedName("lastname") var lastName: String? = null
)