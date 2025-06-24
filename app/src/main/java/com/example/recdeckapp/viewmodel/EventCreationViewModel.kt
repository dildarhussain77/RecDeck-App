package com.example.recdeckapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventEntity
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventInterestCrossRef
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventWithInterests
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
                    Log.d(
                        "EventInsert",
                        "Inserting interests: ${refs.map { "${it.eventId}-${it.categoryId}" }}"
                    )

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

    fun getUsedGroupIds(onResult: (List<Int>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val usedIds = eventDao.getUsedGroupIds()
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
                val eventWithInterests = eventDao.getEventWithInterests(eventId)

                Log.d(
                    "EventDebug",
                    "Interests loaded = ${eventWithInterests?.interests?.map { it.name }}"
                )

                eventWithInterests?.let {
                    withContext(Dispatchers.Main) {
                        isEditing = true
                        currentEventId = eventId
                        eventName = it.event.eventName
                        memberLimit = it.event.memberLimit
                        eventDate = it.event.eventDate
                        eventStartTime = it.event.eventStartTime
                        eventEndTime = it.event.eventEndTime
                        eventDescription = it.event.eventDescription
                        eventImageUrl = it.event.eventImageUrl
                        pitchId = it.event.pitchId
                        groupId = it.event.groupId
                        eventRepeat = it.event.eventRepeat
                        eventPaymentType = it.event.eventPaymentType
                        creatorUserId = it.event.creatorUserId
                        selectedInterests = it.interests
                    }
                }

                return@withContext eventWithInterests
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

