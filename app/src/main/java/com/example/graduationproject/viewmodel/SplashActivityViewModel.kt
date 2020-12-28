package com.example.graduationproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.graduationproject.model.authentication.RefreshToken
import com.example.graduationproject.model.authentication.Token
import com.example.graduationproject.repository.AuthenticationRepository

class SplashActivityViewModel(private val authRepository: AuthenticationRepository): ViewModel() {

    suspend fun refreshToken(refreshToken: RefreshToken): Token?{
        return authRepository.refreshToken(refreshToken)
    }
}