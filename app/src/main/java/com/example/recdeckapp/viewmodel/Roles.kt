package com.example.recdeckapp.viewmodel

import androidx.lifecycle.ViewModel

class ViewModelRole : ViewModel() {
    var RoleSelection: Role? = null
    enum class Role {
        ORGANIZER,
        FACULTY
    }
}