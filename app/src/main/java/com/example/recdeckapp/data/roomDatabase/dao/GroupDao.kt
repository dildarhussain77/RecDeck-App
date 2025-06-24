package com.example.recdeckapp.data.roomDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.GroupCreation.GroupEntity
import com.example.recdeckapp.data.roomDatabase.entities.GroupCreation.GroupInterestCrossRef

@Dao
interface GroupDao {
    @Insert
    suspend fun insertGroup(group: GroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroupInterestsCrossRef(refs: List<GroupInterestCrossRef>)

    @Transaction
    suspend fun insertGroupWithInterests(group: GroupEntity, interests: List<InterestEntity>) {
        val groupId = insertGroup(group).toInt()
        val refs = interests.map {
            GroupInterestCrossRef(groupId = groupId, categoryId = it.categoryId)
        }
        insertGroupInterestsCrossRef(refs)
    }

    @Query("SELECT * FROM groups WHERE creatorUserId = :userId")
    suspend fun getAllGroupsByUser(userId: Int): List<GroupEntity>

    @Transaction
    suspend fun deleteGroupsWithRelations(groupId: Int) {
        // First delete from the cross-ref table
        deleteGroupInterests(groupId)
        // Then delete from the main pitches table
        deleteGroupById(groupId)
    }

    @Query("DELETE FROM groups WHERE groupId = :groupId")
    suspend fun deleteGroupById(groupId: Int)

    @Query("DELETE FROM GroupInterestCrossRef WHERE groupId = :groupId")
    suspend fun deleteGroupInterests(groupId: Int)
}
