package com.example.recdeckapp.data.roomDatabase.entities.SignUp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val country: String,
    val city: String,
    val profilePicPath: String?, // optional
    val role: String // "Organizer" or "Facility"
)
