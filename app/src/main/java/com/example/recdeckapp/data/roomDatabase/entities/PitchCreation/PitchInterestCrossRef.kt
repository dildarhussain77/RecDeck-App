package com.example.recdeckapp.data.roomDatabase.entities.PitchCreation

import androidx.room.Entity

@Entity(primaryKeys = ["pitchId", "categoryId"])
data class PitchInterestCrossRef(
    val pitchId: Int,
    val categoryId: Int
)
