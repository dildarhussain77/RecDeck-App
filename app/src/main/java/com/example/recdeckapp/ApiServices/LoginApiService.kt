package com.example.recdeckapp.ApiServices

import com.example.recdeckapp.data.network.LoginRequest
import com.example.recdeckapp.data.network.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {

    @POST("auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}