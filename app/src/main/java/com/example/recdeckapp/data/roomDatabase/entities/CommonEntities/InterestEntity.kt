package com.example.recdeckapp.data.roomDatabase.entities.CommonEntities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interests")
data class InterestEntity(
    @PrimaryKey(autoGenerate = false)
    val categoryId: Int,
    val name: String?,
    val iconResId: Int // Added to store icon resource
)
