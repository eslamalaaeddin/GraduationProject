package com.example.graduationproject.viewmodel

import androidx.lifecycle.*
import com.example.graduationproject.model.ResponseMessage
import com.example.graduationproject.model.user.User
import com.example.graduationproject.model.user.UserName
import com.example.graduationproject.model.user.UserPassword
import com.example.graduationproject.repository.UserRepository
import kotlinx.coroutines.launch

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