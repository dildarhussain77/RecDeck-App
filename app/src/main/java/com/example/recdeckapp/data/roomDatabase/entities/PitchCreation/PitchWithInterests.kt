package com.example.recdeckapp.data.roomDatabase.entities.PitchCreation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity

data class PitchWithInterests(
    @Embedded val pitch: PitchEntity,
    @Relation(
        parentColumn = "pitchId",
        entityColumn = "categoryId",
        associateBy = Junction(PitchInterestCrossRef::class)
    )
    val interests: List<InterestEntity>
)
