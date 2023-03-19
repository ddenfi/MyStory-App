package com.ddenfi.mystoryapp.remote

import com.ddenfi.mystoryapp.database.LoginResponse
import com.ddenfi.mystoryapp.database.RegisterResponse
import com.ddenfi.mystoryapp.database.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Part ("description") desc: RequestBody,
        @Part file: MultipartBody.Part,
        @Part ("lat") lat:Float?,
        @Part ("lon") lon:Float?
    ): Call<RegisterResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String,
        @Query("location") location:Int = 1
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getAllStoriesPaged(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): StoryResponse
}