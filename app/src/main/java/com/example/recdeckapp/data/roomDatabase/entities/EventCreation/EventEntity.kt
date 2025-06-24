package com.example.recdeckapp.data.roomDatabase.entities.EventCreation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val eventId: Int = 0,
    val eventName: String,
    val memberLimit: Int,
    val eventDate: String,
    val eventStartTime: String,
    val eventEndTime: String,
    val eventDescription: String,
    val eventImageUrl: String?,
    val pitchId: Int = 0,
    val groupId: Int = 0,
    val eventRepeat: String,
    val eventPaymentType: String,
    val creatorUserId: Int // Foreign key link to User
)
