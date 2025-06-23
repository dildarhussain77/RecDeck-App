package com.example.recdeckapp.data.roomDatabase.entities.GroupCreation

import androidx.room.Entity

@Entity(primaryKeys = ["groupId", "categoryId"])
data class GroupInterestCrossRef(
    val groupId: Int,
    val categoryId: Int
)
