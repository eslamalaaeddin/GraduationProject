package com.example.graduationproject.models.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//@Entity(tableName = "users")
data class User(
    var email: String? = null,
    @SerializedName("firstname")
    var firstName: String? = null,
    var id: Long? = null,
    var image: String? = null,
    @SerializedName("lastname")
    var lastName: String? = null
)