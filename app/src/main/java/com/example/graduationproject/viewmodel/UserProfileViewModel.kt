package com.example.graduationproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.user.User
import com.example.graduationproject.model.user.UserName
import com.example.graduationproject.model.user.UserPassword
import com.example.graduationproject.repository.UserRepository

class UserProfileViewModel(private val userRepository: UserRepository): ViewModel() {
    suspend fun getUser(accessToken: String): User?{
        return userRepository.getUser(accessToken)
    }

    suspend fun updateUserName(userName: UserName, accessToken: String): ResponseMessage?{
        return userRepository.updateUserName(userName, accessToken)
    }

    suspend fun updateUserPassword(userPassword: UserPassword, accessToken: String): ResponseMessage?{
        return userRepository.updateUserPassword(userPassword, accessToken)
    }
}