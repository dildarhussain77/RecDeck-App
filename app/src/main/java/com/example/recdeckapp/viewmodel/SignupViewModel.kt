package com.example.recdeckapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recdeckapp.data.UserRole

class SignupViewModel : ViewModel() {

    // Backing field for selected user role
    private val _selectedRole = MutableLiveData(UserRole.NONE)
    val selectedRole: LiveData<UserRole> get() = _selectedRole

    // Function to update role
    fun setUserRole(role: UserRole) {
        _selectedRole.value = role
    }
}
