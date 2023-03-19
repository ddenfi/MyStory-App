package com.ddenfi.mystoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.ddenfi.mystoryapp.repositories.UserRepository

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    private val repo = UserRepository(application)

    fun register (name:String,email:String,pass:String) = repo.register(name,email,pass)



}