package com.example.graduationproject.viewmodels

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.user.User
import com.example.graduationproject.models.user.UserName
import com.example.graduationproject.models.user.UserPassword
import com.example.graduationproject.repository.UserRepository

class UserProfileViewModel(private val userRepository: UserRepository): ViewModel() {
    suspend fun getUser(accessToken: String): User?{
        return userRepository.getUser(accessToken)
    }

    suspend fun updateUserName(userName: UserName, accessToken: String): ResponseMessage?{
        return userRepository.updateUserName(userName, accessToken)
    }

    suspend fun updateUserPassword(userPassword: UserPassword, accessToken: String): ResponseMessage?{
        return userRepository.changeUserPassword(userPassword, accessToken)
    }
}