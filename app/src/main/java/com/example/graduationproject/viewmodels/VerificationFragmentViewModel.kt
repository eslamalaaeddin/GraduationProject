package com.example.graduationproject.viewmodels

import androidx.lifecycle.ViewModel
import com.example.graduationproject.models.authentication.Token
import com.example.graduationproject.models.authentication.Verify
import com.example.graduationproject.repository.AuthenticationRepository

class VerificationFragmentViewModel(private val authRepository: AuthenticationRepository): ViewModel() {

    suspend fun verifyUser(verify: Verify): Token?{
        return authRepository.verifyUser(verify)
    }
}