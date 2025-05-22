package com.example.recdeckapp.data.entities

data class Event(
    val imageResId: Int,
    val name: String,
    val date: String,
    val time: String,
    val price: String,
    val totalAttendees: Int,
    val attendees: List<Attendee>
)

data class Attendee(
    val drawableResId: Int
)
enum class EventType(val displayName: String) {
    MY_EVENTS("My Events"),
    SAVED_EVENTS("Save Events"),
    PAST_EVENTS("Past Events"),
    JOINED_EVENTS("Joined Events"),
    CANCELED_EVENTS("Cancel Events")
}

data class EventCategory(
    val type: EventType,
    val events: List<Event>
)