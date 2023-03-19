package com.ddenfi.mystoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ddenfi.mystoryapp.repositories.UserRepository

class LoginViewModel(application: Application):AndroidViewModel(application) {
    private val repo = UserRepository(application)

    fun login(email:String,pass:String) = repo.login(email,pass)

    suspend fun saveTokenAuth(tokenAuth:String) = repo.saveTokenAuth(tokenAuth)
    suspend fun saveLoginState(isLogin:Boolean) = repo.saveLoginState(isLogin)
}