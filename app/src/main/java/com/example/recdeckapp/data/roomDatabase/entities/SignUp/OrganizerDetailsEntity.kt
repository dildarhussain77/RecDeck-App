package com.example.recdeckapp.data.roomDatabase.entities.SignUp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "organizer_details",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class OrganizerDetailsEntity(
    @PrimaryKey
    val userId: Int,
    val dateOfBirth: String?, // optional
    val gender: String?       // optional
)
