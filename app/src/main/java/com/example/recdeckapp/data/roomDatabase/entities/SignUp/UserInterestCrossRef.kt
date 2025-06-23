package com.example.recdeckapp.data.roomDatabase.entities.SignUp

import androidx.room.Entity

@Entity(
    primaryKeys = ["userId", "categoryId"]
)
data class UserInterestCrossRef(
    val userId: Int,
    val categoryId: Int
)
