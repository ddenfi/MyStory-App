package com.ddenfi.mystoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ddenfi.mystoryapp.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = UserRepository(application)

    fun getToken() = repo.getTokenAuth().asLiveData(Dispatchers.IO)

    fun getStory(token:String) = repo.getStoryListPaged("Bearer $token")

    fun resetSavedData(){
        viewModelScope.launch{
            repo.saveLoginState(false)
            repo.saveTokenAuth("")
            repo.deleteStoryDatabase()
            repo.deleteRemoteKeyDatabase()
        }
    }
}