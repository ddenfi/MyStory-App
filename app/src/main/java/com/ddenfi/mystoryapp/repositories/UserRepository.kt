package com.ddenfi.mystoryapp.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.ddenfi.mystoryapp.database.*
import com.ddenfi.mystoryapp.remote.ApiConfig
import com.ddenfi.mystoryapp.remote.ApiService
import com.ddenfi.mystoryapp.utils.Results
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(application: Application) {
    private val storyDatabase = StoryDatabase.getDatabase(application)
    private val storyDao = storyDatabase.storyDao()
    private val remoteKeysDao = storyDatabase.remoteKeysDao()
    private val dataStore: SettingPreferences = SettingPreferences.getInstance(application)
    private val retrofit: ApiService = ApiConfig.getApiService()

    fun getStoryListPaged(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(apiService = retrofit, database = storyDatabase, token = token),
            pagingSourceFactory = {
                storyDao.getAllStory()
            }
        ).liveData
    }

    fun register(name: String, email: String, pass: String): LiveData<Results<RegisterResponse>> {
        val result = MutableLiveData<Results<RegisterResponse>>()

        result.postValue(Results.Loading())
        retrofit.register(name, email, pass).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error)
                        result.postValue(Results.Success(responseBody))
                } else {
                    val jsonObject =
                        JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    result.postValue(Results.Error(message))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                result.postValue(Results.Error(t.message))
                Log.d("Register", "Failure ${t.message}")
            }
        })
        return result
    }

    fun login(email: String, pass: String): LiveData<Results<LoginResponse>> {
        val result = MutableLiveData<Results<LoginResponse>>()

        result.postValue(Results.Loading())
        retrofit.login(email, pass).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error)
                        result.postValue(Results.Success(responseBody))
                } else {
                    val jsonObject =
                        JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    result.postValue(Results.Error(message))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                result.postValue(Results.Error(t.message))
                Log.d("Register", "Failure ${t.message}")
            }

        })

        return result
    }

    fun getStoryList(token: String): LiveData<Results<StoryResponse>> {
        val result = MutableLiveData<Results<StoryResponse>>()

        result.postValue(Results.Loading())
        retrofit.getAllStories("Bearer $token").enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        result.postValue(Results.Success(responseBody))
                    }
                } else {
                    val jsonObject =
                        JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    result.postValue(Results.Error(message))
                    Log.d("getStory", "error $message")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                result.postValue(Results.Error(t.message))
                Log.d("getStory", "Failure ${t.message}")
            }

        })
        return result
    }

    fun upload(imageMultipart: MultipartBody.Part,description:RequestBody,token:String,lat:Float?,lon:Float?):LiveData<Results<RegisterResponse>>{
        val result = MutableLiveData<Results<RegisterResponse>>()

        result.postValue(Results.Loading())
        retrofit.addStories("Bearer $token",description,imageMultipart,lat,lon).enqueue(object :Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        result.postValue(Results.Success(responseBody))
                    }
                } else {
                    val jsonObject =
                        JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                    val message = jsonObject.getString("message")
                    result.postValue(Results.Error(message))
                    Log.d("getStory", "error $message")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                result.postValue(Results.Error(t.message))
                Log.d("getStory", "Failure ${t.message}")
            }

        })
        return result
    }

    fun getLoginState() = dataStore.getLoginState()

    fun getTokenAuth() = dataStore.getTokenAuth()

    suspend fun saveLoginState(isLogin: Boolean) = dataStore.saveLoginState(isLogin)

    suspend fun saveTokenAuth(tokenAuth: String) = dataStore.saveTokenAuth(tokenAuth)

    suspend fun deleteStoryDatabase() = storyDao.deleteAll()

    suspend fun deleteRemoteKeyDatabase() = remoteKeysDao.deleteRemoteKeys()
}