package com.example.graduationproject.viewmodels

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.authentication.RefreshToken
import com.example.graduationproject.models.authentication.Token
import com.example.graduationproject.repository.AuthenticationRepository

class SplashActivityViewModel(private val authRepository: AuthenticationRepository): ViewModel() {

    suspend fun refreshToken(refreshToken: RefreshToken): Token?{
        return authRepository.refreshToken(refreshToken)
    }
}