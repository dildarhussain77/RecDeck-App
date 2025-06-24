package com.example.recdeckapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recdeckapp.R
import com.example.recdeckapp.data.entities.Attendee
import com.example.recdeckapp.data.entities.Event
import com.example.recdeckapp.data.entities.EventCategory
import com.example.recdeckapp.data.entities.EventType

class EventsViewModel : ViewModel() {
    private val _selectedEventType = MutableLiveData<EventType>()
    val selectedEventType: LiveData<EventType> = _selectedEventType
    private val _events = MutableLiveData<List<EventCategory>>()
    private val _selectedEvents = MediatorLiveData<List<Event>>()
    val selectedEvents: LiveData<List<Event>> = _selectedEvents

    init {
        _events.value = generateDummyEvents()
        _selectedEventType.value = EventType.MY_EVENTS
        _selectedEvents.addSource(_selectedEventType) { updateSelectedEvents() }
        _selectedEvents.addSource(_events) { updateSelectedEvents() }
    }

    fun selectEventType(type: EventType) {
        _selectedEventType.value = type
    }

    private fun updateSelectedEvents() {
        val type = _selectedEventType.value
        val list = _events.value
        _selectedEvents.value = list?.find { it.type == type }?.events ?: emptyList()
    }

    private fun generateDummyEvents(): List<EventCategory> {
        fun dummyAttendees(count: Int) = List(count) {
            Attendee(
                drawableResId = when (it) {
                    0 -> R.drawable.cricket
                    1 -> R.drawable.basket_ball
                    2 -> R.drawable.hockey
                    else -> R.drawable.horse_ride
                }
            )
        }

        fun dummyEventList(type: EventType) = List(5) {
            Event(
                imageResId = R.drawable.img_football,
                //name = "${type.displayName} Event ${it + 1}",
                name = "Lorem ipsum dolor sit amet consectetur",
                date = "2025-09-1${it}",
                time = "10:${10 + it} AM",
                //price = "$${10 + it * 2}",
                price = "SAR 2,00/ per head",
                totalAttendees = (2..6).random(),
                attendees = dummyAttendees((2..6).random())
            )
        }
        return EventType.values().map { type ->
            EventCategory(type, dummyEventList(type))
        }
    }
}
