package com.example.recdeckapp.data.roomDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventEntity
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventInterestCrossRef
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventWithInterests

@Dao
interface EventDao {

    @Insert
    suspend fun insertEvent(group: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEventInterestsCrossRef(refs: List<EventInterestCrossRef>)

    @Transaction
    suspend fun insertEventWithInterests(event: EventEntity, interests: List<InterestEntity>) {
        val eventId = insertEvent(event).toInt()
        val refs = interests.map {
            EventInterestCrossRef(eventId = eventId, categoryId = it.categoryId)
        }
        insertEventInterestsCrossRef(refs)
    }

    @Query("SELECT pitchId FROM events") // Adjust your table name
    suspend fun getUsedPitchIds(): List<Int>

    @Transaction
    @Query("SELECT * FROM events WHERE creatorUserId = :userId")
    suspend fun getEventsCreatedByUser(userId: Int): List<EventWithInterests>

    @Query("SELECT * FROM events WHERE creatorUserId = :userId")
    suspend fun getAllEventsByUser(userId: Int): List<EventEntity>

    @Transaction
    suspend fun deleteEventWithRelations(eventId: Int) {
        // First delete from the cross-ref table
        deleteEventInterests(eventId)
        // Then delete from the main events table
        deleteEventById(eventId)
    }

    @Query("DELETE FROM events WHERE eventId = :eventId")
    suspend fun deleteEventById(eventId: Int)

    @Query("DELETE FROM EventInterestCrossRef WHERE eventId = :eventId")
    suspend fun deleteEventInterests(eventId: Int)

    @Update
    suspend fun updateEvent(event: EventEntity)
}