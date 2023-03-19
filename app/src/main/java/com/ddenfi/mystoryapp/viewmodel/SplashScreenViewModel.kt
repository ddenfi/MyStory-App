package com.ddenfi.mystoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.ddenfi.mystoryapp.repositories.UserRepository
import kotlinx.coroutines.Dispatchers

class SplashScreenViewModel(application: Application) :AndroidViewModel(application) {
    private val repo = UserRepository(application)

    fun getLoginState() = repo.getLoginState().asLiveData(Dispatchers.IO)
}