package com.example.recdeckapp.RetrofitClient

import com.example.recdeckapp.ApiServices.LoginApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginRetrofitClient {
    private const val BASE_URL = "https://dummyjson.com/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: LoginApiService by lazy {
        instance.create(LoginApiService::class.java)
    }
}
