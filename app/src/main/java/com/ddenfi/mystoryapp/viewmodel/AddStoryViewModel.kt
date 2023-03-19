package com.ddenfi.mystoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.ddenfi.mystoryapp.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository(application)

    fun upload(imageMultipart: MultipartBody.Part, description: RequestBody, token: String,lat:Float?,lon:Float?) =
        repo.upload(imageMultipart, description, token,lat,lon)

    fun getToken() = repo.getTokenAuth().asLiveData(Dispatchers.IO)
}