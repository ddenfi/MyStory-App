package com.ddenfi.mystoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope

import com.ddenfi.mystoryapp.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel(application: Application): AndroidViewModel(application) {
    private val repo = UserRepository(application)

    fun getMapStories(token:String) = repo.getStoryList(token)
    fun getToken() = repo.getTokenAuth().asLiveData(Dispatchers.Main)

    fun saveUserState (token: String, loginState: Boolean) = viewModelScope.launch{
        repo.saveLoginState(loginState)
        repo.saveTokenAuth(token)
    }
}