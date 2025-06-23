package com.example.recdeckapp.data.roomDatabase.entities.GroupCreation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity

data class GroupWithInterests(
    @Embedded val group: GroupEntity,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "categoryId",
        associateBy = Junction(GroupInterestCrossRef::class)
    )
    val interests: List<InterestEntity>
)
