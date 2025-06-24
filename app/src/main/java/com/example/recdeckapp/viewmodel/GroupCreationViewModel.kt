package com.example.recdeckapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.GroupCreation.GroupEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupCreationViewModel(application: Application) : AndroidViewModel(application) {
    private val groupDao = AppDatabase.getDatabase(application).groupDao()

    // Store fields
    var groupName: String = ""
    var memberLimit: Int = 0
    var accessType: String = "Public"
    var description: String = ""
    var rules: String = ""
    var imageUrl: String? = null
    var creatorUserId: Int = -1
    var selectedInterests: List<InterestEntity> = emptyList()
    fun insertGroupToRoom(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val group = GroupEntity(
                    groupName = groupName,
                    memberLimit = memberLimit,
                    accessibility = accessType,
                    description = description,
                    rules = rules,
                    imageUrl = imageUrl,
                    creatorUserId = creatorUserId,
                    isGroupAvailable = true
                )
                groupDao.insertGroupWithInterests(group, selectedInterests)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun getAllUserGroups(userId: Int, onResult: (List<GroupEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val groups = groupDao.getAllGroupsByUser(userId)
                withContext(Dispatchers.Main) {
                    onResult(groups)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(emptyList())
                }
            }
        }
    }

    fun isGroupAlreadyCreated(userId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val groups = groupDao.getAllGroupsByUser(userId)
                callback(groups.isNotEmpty())
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }
    }

    fun deleteGroup(groupId: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            // Use the new transaction method
            groupDao.deleteGroupsWithRelations(groupId)
            onDeleted()
        }
    }
}
