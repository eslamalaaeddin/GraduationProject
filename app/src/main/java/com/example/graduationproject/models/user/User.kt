package com.example.graduationproject.models.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    var email: String? = null,
    @SerializedName("firstname")
    @ColumnInfo(name = "first_name")
    var firstName: String? = null,
    @PrimaryKey var id: Long? = null,
    var image: String? = null,
    @SerializedName("lastname")
    @ColumnInfo(name = "last_name")
    var lastName: String? = null
)