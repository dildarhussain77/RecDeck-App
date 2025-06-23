package com.example.recdeckapp.data.roomDatabase.entities.EventCreation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity

data class EventWithInterests(
    @Embedded val event: EventEntity,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "categoryId",
        associateBy = Junction(EventInterestCrossRef::class)
    )
    val interests: List<InterestEntity>
)
