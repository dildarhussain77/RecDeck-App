package com.example.recdeckapp.data.roomDatabase.AppDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recdeckapp.data.roomDatabase.dao.EventDao
import com.example.recdeckapp.data.roomDatabase.dao.GroupDao
import com.example.recdeckapp.data.roomDatabase.dao.PitchDao
import com.example.recdeckapp.data.roomDatabase.dao.UserDao
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventEntity
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventInterestCrossRef
import com.example.recdeckapp.data.roomDatabase.entities.GroupCreation.GroupEntity
import com.example.recdeckapp.data.roomDatabase.entities.GroupCreation.GroupInterestCrossRef
import com.example.recdeckapp.data.roomDatabase.entities.PitchCreation.PitchEntity
import com.example.recdeckapp.data.roomDatabase.entities.PitchCreation.PitchInterestCrossRef
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.Converters
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.FacilityDetailsEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.OrganizerDetailsEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserInterestCrossRef
import com.example.recdeckapp.utils.InterestItemsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        OrganizerDetailsEntity::class,
        FacilityDetailsEntity::class,
        InterestEntity::class,
        UserInterestCrossRef::class,
        GroupEntity::class,
        GroupInterestCrossRef::class,
        EventEntity::class,
        EventInterestCrossRef::class,
        PitchEntity::class,
        PitchInterestCrossRef::class
    ],
    version = 8  // Incremented version number
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun eventDao(): EventDao
    abstract fun pitchDao(): PitchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recdeck_dp"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration() // Only for development!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // Using IO dispatcher because database operations shouldn't run on main thread
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                populateInitialInterests(database.userDao())
            }
        }

        private suspend fun populateInitialInterests(dao: UserDao) {
            // First check if interests already exist
            val existingCount = dao.getInterestsCount()
            if (existingCount == 0) {
                // Sample data
                val defaultInterests = InterestItemsProvider.getDefaultInterestItemsAppDatabase()
                dao.insertInterests(defaultInterests)
            }
        }
    }
}