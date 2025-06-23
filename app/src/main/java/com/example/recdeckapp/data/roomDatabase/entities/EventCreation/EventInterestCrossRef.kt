package com.example.recdeckapp.data.roomDatabase.entities.EventCreation

import androidx.room.Entity

@Entity(primaryKeys = ["eventId", "categoryId"])
data class EventInterestCrossRef(
    val eventId: Int,
    val categoryId: Int
)

