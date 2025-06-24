package com.example.recdeckapp.data.roomDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.FacilityDetailsEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.OrganizerDetailsEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserInterestCrossRef
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserWithDetailsAndInterests

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Insert
    suspend fun insertOrganizerDetails(details: OrganizerDetailsEntity)

    @Insert
    suspend fun insertFacilityDetails(details: FacilityDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInterests(interests: List<InterestEntity>)

    @Query("SELECT * FROM interests")
    suspend fun getAllInterests(): List<InterestEntity>

    @Query("SELECT COUNT(*) FROM interests")
    suspend fun getInterestsCount(): Int

    @Insert
    suspend fun insertUserInterestCrossRefs(crossRefs: List<UserInterestCrossRef>)

    @Query("SELECT COUNT(*) FROM interests WHERE categoryId = :id")
    suspend fun interestExists(id: Int): Int

    @Transaction
    @Query("SELECT * FROM UserInterestCrossRef WHERE userId = :userId")
    suspend fun getUserInterestCrossRefs(userId: Int): List<UserInterestCrossRef>

    @Transaction
    suspend fun insertUserWithInterests(
        user: UserEntity,
        organizerDetails: OrganizerDetailsEntity? = null,
        facilityDetails: FacilityDetailsEntity? = null,
        interests: List<InterestEntity> = emptyList()
    ) {
        // Insert user and get ID
        val userId = insertUser(user).toInt()
        // Insert role details
        organizerDetails?.let { insertOrganizerDetails(it.copy(userId = userId)) }
        facilityDetails?.let { insertFacilityDetails(it.copy(userId = userId)) }
        // Insert interests if any
        if (interests.isNotEmpty()) {
            // First check which interests don't exist yet
            val existingIds = getAllInterests().map { it.categoryId }
            val newInterests = interests.filter { !existingIds.contains(it.categoryId) }
            if (newInterests.isNotEmpty()) {
                insertInterests(newInterests)
            }
            // Create cross-references (no duplicates)
            val existingRefs = getUserInterestCrossRefs(userId)
            val newRefs = interests.map { UserInterestCrossRef(userId, it.categoryId) }
                .filter { newRef ->
                    !existingRefs.any { it.userId == newRef.userId && it.categoryId == newRef.categoryId }
                }
            if (newRefs.isNotEmpty()) {
                insertUserInterestCrossRefs(newRefs)
            }
        }
    }

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Int

    @Transaction
    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserWithDetailsAndInterests(id: Int): UserWithDetailsAndInterests
}
