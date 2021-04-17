package com.example.graduationproject.models.user

import com.google.gson.annotations.SerializedName

data class UserPassword(
    @SerializedName("oldpassword") var oldPassword: String? = null,
    @SerializedName("newpassword") var newPassword: String? = null
)