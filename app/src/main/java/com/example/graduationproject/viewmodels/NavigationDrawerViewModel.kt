package com.example.graduationproject.viewmodels

import androidx.lifecycle.*
import com.example.graduationproject.models.ResponseMessage
import com.example.graduationproject.models.user.User
import com.example.graduationproject.models.user.UserName
import com.example.graduationproject.models.user.UserPassword
import com.example.graduationproject.repository.UserRepository

class NavigationDrawerViewModel(private val usersRepository: UserRepository): ViewModel() {

    suspend fun getUser(accessToken: String): User?{
        return usersRepository.getUser(accessToken)
    }

    //another option
    fun getUserWithViewModelScope(accessToken: String): LiveData<User?>{
        return liveData {
            val data = usersRepository.getUser(accessToken)
            emit(data)
        }
//         viewModelScope.launch {
//             user = usersRepository.getUser(accessToken)
//        }
//        return user
    }

    //I think that second solution will be with deferred, without using live data

    suspend fun updateUserName(userName: UserName, accessToken: String): ResponseMessage?{
        return usersRepository.updateUserName(userName, accessToken)
    }

    suspend fun changeUserPassword(userPassword: UserPassword, accessToken: String): ResponseMessage?{
        return usersRepository.changeUserPassword(userPassword, accessToken)
    }
}