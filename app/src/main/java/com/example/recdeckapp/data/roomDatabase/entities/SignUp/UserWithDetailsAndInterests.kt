package com.example.recdeckapp.data.roomDatabase.entities.SignUp

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity

data class UserWithDetailsAndInterests(
    @Embedded
    val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val organizerDetails: OrganizerDetailsEntity?, // null if not organizer
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val facilityDetails: FacilityDetailsEntity?, // null if not facility
    @Relation(
        parentColumn = "userId", // from UserEntity
        entity = InterestEntity::class,
        entityColumn = "categoryId", // from InterestEntity
        associateBy = Junction(
            value = UserInterestCrossRef::class,
            parentColumn = "userId", // from UserInterestCrossRef
            entityColumn = "categoryId" // from UserInterestCrossRef
        )
    )
    val interests: List<InterestEntity>
)
