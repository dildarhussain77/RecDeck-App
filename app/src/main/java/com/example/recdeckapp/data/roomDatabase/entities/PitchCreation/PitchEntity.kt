package com.example.recdeckapp.data.roomDatabase.entities.PitchCreation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pitches")
data class PitchEntity(
    @PrimaryKey(autoGenerate = true)
    val pitchId: Int = 0,
    val pitchName: String,
    val pitchStartTime: String,
    val pitchEndTime: String,
    val pitchDescription: String,
    val pitchFacilityImageUrl: String?,
    val pitchFacilityName: String,
    val isPitchAvailable: Boolean = true,
    val pitchIdPass: String,
    val pitchDocPaths: List<String>,
    val creatorUserId: Int // Foreign key link to User
)
