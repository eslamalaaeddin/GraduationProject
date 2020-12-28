package com.example.graduationproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.authentication.SignUp
import com.example.graduationproject.repository.AuthenticationRepository
import java.net.Authenticator

/*
    I have a question, why do i need a repository if i have no data to deal with !
    but, i wil do it as i need to implement MVVM as it in google docs  UI --> VM --> Repo --> (Local, Remote)

 */
class SignUpViewModel(private val authRepository: AuthenticationRepository): ViewModel() {
    suspend fun signUp(signUp: SignUp): ResponseMessage?{
        return authRepository.signUp(signUp)
    }
}