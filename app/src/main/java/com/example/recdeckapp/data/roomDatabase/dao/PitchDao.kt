package com.example.recdeckapp.data.roomDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.PitchCreation.PitchEntity
import com.example.recdeckapp.data.roomDatabase.entities.PitchCreation.PitchInterestCrossRef

@Dao
interface PitchDao {
    @Insert
    suspend fun insertPitch(pitch: PitchEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPitchInterestsCrossRef(refs: List<PitchInterestCrossRef>)

    @Transaction
    suspend fun insertPitchWithInterests(pitch: PitchEntity, interests: List<InterestEntity>) {
        val pitchId = insertPitch(pitch).toInt()
        val refs = interests.map {
            PitchInterestCrossRef(pitchId = pitchId, categoryId = it.categoryId)
        }
        insertPitchInterestsCrossRef(refs)
    }

    @Query("SELECT * FROM pitches WHERE creatorUserId = :userId")
    suspend fun getAllPitchesByUser(userId: Int): List<PitchEntity>

    @Transaction
    suspend fun deletePitchWithRelations(pitchId: Int) {
        // First delete from the cross-ref table
        deletePitchInterests(pitchId)
        // Then delete from the main pitches table
        deletePitchById(pitchId)
    }

    @Query("DELETE FROM pitches WHERE pitchId = :pitchId")
    suspend fun deletePitchById(pitchId: Int)

    @Query("DELETE FROM PitchInterestCrossRef WHERE pitchId = :pitchId")
    suspend fun deletePitchInterests(pitchId: Int)
}