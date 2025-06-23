package com.example.recdeckapp.data.roomDatabase.entities.GroupCreation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val groupId: Int = 0,
    val groupName: String,
    val memberLimit: Int,
    val accessibility: String, // "private" or "public"
    val description: String,
    val rules: String,
    val isGroupAvailable: Boolean = true,
    val imageUrl: String?,
    val creatorUserId: Int // Foreign key link to User
)
