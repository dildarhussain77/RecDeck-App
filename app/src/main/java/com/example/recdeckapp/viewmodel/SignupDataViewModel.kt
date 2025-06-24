package com.example.recdeckapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recdeckapp.data.entities.CardItemIntrests
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.FacilityDetailsEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.OrganizerDetailsEntity
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserEntity
import com.example.recdeckapp.data.userRole.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupDataViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    var selectedRole: UserRole? = null
    var fullName: String = ""
    var email: String = ""
    var password: String = ""
    var country: String = ""
    var city: String = ""
    var profilePicPath: String? = null

    //  Organizer Fields
    var dob: String? = null
    var gender: String? = null

    //  Facility Fields
    var description: String? = null
    var idOrPassport: String? = null
    var docFilePaths: List<String> = emptyList()

    // Interests
    var selectedInterests: List<CardItemIntrests> = emptyList()

    // In SignupDataViewModel
    fun addDocumentPath(path: String) {
        docFilePaths = docFilePaths + path
    }

    fun removeDocumentPath(path: String) {
        docFilePaths = docFilePaths.filter { it != path }
    }

    var userId: Int = -1  // Default invalid value
    fun insertAllDataToRoom(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("SignupDebug", "Signup Data: Name: $fullName, Email: $email")
                // Prepare data
                val user = UserEntity(
                    fullName = fullName,
                    email = email,
                    password = password,
                    country = country,
                    city = city,
                    profilePicPath = profilePicPath,
                    role = selectedRole.toString()
                )
                val organizerDetails = if (selectedRole == UserRole.ORGANIZER) {
                    OrganizerDetailsEntity(
                        userId = 0, // Temporary, will be updated
                        dateOfBirth = dob,
                        gender = gender
                    )
                } else null
                val facilityDetails = if (selectedRole == UserRole.FACILITY) {
                    FacilityDetailsEntity(
                        userId = 0, // Temporary
                        description = description,
                        idOrPassportNumber = idOrPassport,
                        documentFilePaths = docFilePaths
                    )
                } else null
                val interestEntities = selectedInterests.map {
                    InterestEntity(
                        categoryId = it.id,
                        name = it.title,
                        iconResId = it.imageResource
                    )
                }
                // Single transaction
                userDao.insertUserWithInterests(
                    user = user,
                    organizerDetails = organizerDetails,
                    facilityDetails = facilityDetails,
                    interests = interestEntities
                )
                // Verify
                val insertedUser =
                    userDao.getUserByEmail(email) ?: throw Exception("User not found")
                userId = insertedUser.userId
                Log.d("SignupDebug", "User inserted with ID: $userId")
                onResult(true)
            } catch (e: Exception) {
                Log.e("SignupError", "Signup failed", e)
                onResult(false)
            }
        }
    }

    // In SignupDataViewModel.kt
    suspend fun isEmailAvailable(email: String): Boolean {
        return userDao.emailExists(email) == 0
    }
}
