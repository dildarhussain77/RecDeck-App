package com.example.recdeckapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    val userLiveData = MutableLiveData<UserEntity>()
    fun getUserData(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserById(userId)
            user?.let {
                userLiveData.postValue(it)
            }
        }
    }
}
