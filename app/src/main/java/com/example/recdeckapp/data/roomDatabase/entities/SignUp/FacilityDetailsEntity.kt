package com.example.recdeckapp.data.roomDatabase.entities.SignUp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(
    tableName = "facility_details",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FacilityDetailsEntity(
    @PrimaryKey
    val userId: Int,
    val description: String?,         // optional
    val idOrPassportNumber: String?, // optional
    //val documentFilePath: String?    // optional
    val documentFilePaths: List<String> // changed from single String? to List<String>
)

@TypeConverters(Converters::class)
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String =
        Gson().toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
}
