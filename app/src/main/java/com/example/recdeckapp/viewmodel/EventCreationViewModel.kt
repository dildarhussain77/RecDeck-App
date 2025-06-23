package com.example.recdeckapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventEntity
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventInterestCrossRef
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventWithInterests
import com.example.recdeckapp.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventCreationViewModel(application: Application) : AndroidViewModel(application) {

    private val eventDao = AppDatabase.getDatabase(application).eventDao()

    // Store fields
    var eventName: String = ""
    var memberLimit: Int = 0
    var eventDate: String = ""
    var eventStartTime: String = ""
    var eventEndTime: String = ""
    var eventDescription: String = ""
    var eventImageUrl: String? = null
    var pitchId: Int = -1
    var groupId: Int = -1
    var eventRepeat: String = ""
    var eventPaymentType: String = ""
    var creatorUserId: Int = -1
    var selectedInterests: List<InterestEntity> = emptyList()

    // variables to track editing state
    var isEditing: Boolean = false
    var currentEventId: Int = -1

    fun saveOrUpdateEvent(onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val event = EventEntity(
                    eventId = if (isEditing) currentEventId else 0,
                    eventName = eventName,
                    memberLimit = memberLimit,
                    eventDate = eventDate,
                    eventStartTime = eventStartTime,
                    eventEndTime = eventEndTime,
                    eventDescription = eventDescription,
                    eventImageUrl = eventImageUrl,
                    pitchId = pitchId,
                    groupId = groupId,
                    eventRepeat = eventRepeat,
                    eventPaymentType = eventPaymentType,
                    creatorUserId = creatorUserId,
                )

                if (isEditing) {
                    // For update, we need to:
                    // 1. Update the event
                    // 2. Delete old interests
                    // 3. Add new interests
                    eventDao.updateEvent(event)
                    eventDao.deleteEventInterests(event.eventId)
                    val refs = selectedInterests.map {
                        EventInterestCrossRef(eventId = event.eventId, categoryId = it.categoryId)
                    }
                    if (refs.isNotEmpty()) {
                        eventDao.insertEventInterestsCrossRef(refs)
                    }
                } else {
                    // Original insert logic
                    eventDao.insertEventWithInterests(event, selectedInterests)
                }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun getUsedPitchIds(onResult: (List<Int>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val usedIds = eventDao.getUsedPitchIds()
            onResult(usedIds)
        }
    }

    fun getAllUserEvents(userId: Int, onResult: (List<EventEntity>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val events = eventDao.getAllEventsByUser(userId)
                withContext(Dispatchers.Main) {
                    onResult(events)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(emptyList())
                }
            }
        }
    }

    fun deleteEvent(eventId: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            // Use the new transaction method
            eventDao.deleteEventWithRelations(eventId)
            onDeleted()
        }
    }

    //loadEventForEditing to properly handle loading state
    suspend fun loadEventForEditing(eventId: Int): EventWithInterests? {
        return withContext(Dispatchers.IO) {
            try {
                val events = eventDao.getEventsCreatedByUser(SessionManager.getUserId(application))
                events.find { it.event.eventId == eventId }?.also { eventWithInterests ->
                    // Post the values to LiveData to ensure UI consistency
                    withContext(Dispatchers.Main) {
                        isEditing = true
                        currentEventId = eventId
                        // Set all fields
                        eventName = eventWithInterests.event.eventName
                        memberLimit = eventWithInterests.event.memberLimit
                        eventDate = eventWithInterests.event.eventDate
                        eventStartTime = eventWithInterests.event.eventStartTime
                        eventEndTime = eventWithInterests.event.eventEndTime
                        eventDescription = eventWithInterests.event.eventDescription
                        eventImageUrl = eventWithInterests.event.eventImageUrl
                        pitchId = eventWithInterests.event.pitchId
                        groupId = eventWithInterests.event.groupId
                        eventRepeat = eventWithInterests.event.eventRepeat
                        eventPaymentType = eventWithInterests.event.eventPaymentType
                        creatorUserId = eventWithInterests.event.creatorUserId
                        selectedInterests = eventWithInterests.interests
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    //to clear viewmodel state when done
    fun clearEditingState() {
        isEditing = false
        currentEventId = -1
        // Clear all other fields if needed
    }
}

