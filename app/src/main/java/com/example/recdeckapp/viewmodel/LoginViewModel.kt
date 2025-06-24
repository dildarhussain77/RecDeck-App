package com.example.recdeckapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserEntity

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    suspend fun loginUser(email: String, password: String): UserEntity? {
        return userDao.getUserByEmailAndPassword(email, password)
    }
}