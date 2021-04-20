package com.example.graduationproject.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.graduationproject.models.user.User

@Dao
interface UsersDao {

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUser(userId: Long) : User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}