package com.example.graduationproject.viewmodels

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.authentication.Login
import com.example.graduationproject.models.authentication.Token
import com.example.graduationproject.repository.AuthenticationRepository

class LoginViewModel(private val authRepository: AuthenticationRepository): ViewModel() {
    suspend fun login(login: Login): Token?{
        return authRepository.login(login)
    }
}