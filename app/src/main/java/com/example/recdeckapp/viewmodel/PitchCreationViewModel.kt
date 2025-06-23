package com.example.recdeckapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.PitchCreation.PitchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PitchCreationViewModel(application: Application) : AndroidViewModel(application) {
    private val pitchDao = AppDatabase.getDatabase(application).pitchDao()

    // Store fields
    var pitchName: String = ""
    var pitchStartTime: String = ""
    var pitchEndTime: String = ""
    var pitchDescription: String = ""
    var pitchFacilityImageUrl: String? = null
    var pitchFacilityName: String = ""
    var creatorUserId: Int = -1
    var selectedInterests: List<InterestEntity> = emptyList()
    var pitchIdPass: String = ""
    var pitchDocPaths: List<String> = emptyList()

    fun addDocumentPath(path: String) {
        pitchDocPaths = pitchDocPaths + path
    }

    fun removeDocumentPath(path: String) {
        pitchDocPaths = pitchDocPaths.filter { it != path }
    }


    fun insertPitchToRoom(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pitch = PitchEntity(
                    pitchName = pitchName,
                    pitchStartTime = pitchStartTime,
                    pitchEndTime = pitchEndTime,
                    pitchDescription = pitchDescription,
                    pitchFacilityImageUrl = pitchFacilityImageUrl,
                    pitchFacilityName = pitchFacilityName,
                    creatorUserId = creatorUserId,
                    pitchIdPass = pitchIdPass,
                    pitchDocPaths = pitchDocPaths,
                    isPitchAvailable = true
                )
                pitchDao.insertPitchWithInterests(pitch, selectedInterests)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun getAllUserPitches(userId: Int, onResult: (List<PitchEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pitches = pitchDao.getAllPitchesByUser(userId)
                withContext(Dispatchers.Main) {
                    onResult(pitches)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(emptyList())
                }
            }
        }
    }

    val selectedPitch = MutableLiveData<PitchEntity>()

    fun setSelectedPitch(pitch: PitchEntity) {
        selectedPitch.value = pitch
    }

    fun deletePitch(pitchId: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            // Use the new transaction method
            pitchDao.deletePitchWithRelations(pitchId)
            onDeleted()
        }
    }


}