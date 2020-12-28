package com.example.graduationproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.graduationproject.model.authentication.Login
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.repository.AuthenticationRepository

class LoginViewModel(private val authRepository: AuthenticationRepository): ViewModel() {
    suspend fun login(login: Login): Token?{
        return authRepository.login(login)
    }
}