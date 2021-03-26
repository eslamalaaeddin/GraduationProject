package com.example.graduationproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.user.User
import com.example.graduationproject.model.user.UserName
import com.example.graduationproject.model.user.UserPassword
import com.example.graduationproject.repository.UserRepository

class NavigationDrawerViewModel(private val usersRepository: UserRepository): ViewModel() {

    suspend fun getUser(accessToken: String): User?{
        return usersRepository.getUser(accessToken)
    }

    suspend fun updateUserName(userName: UserName, accessToken: String): ResponseMessage?{
        return usersRepository.updateUserName(userName, accessToken)
    }

    suspend fun changeUserPassword(userPassword: UserPassword, accessToken: String): ResponseMessage?{
        return usersRepository.changeUserPassword(userPassword, accessToken)
    }
}