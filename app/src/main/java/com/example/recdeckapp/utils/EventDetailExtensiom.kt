package com.example.recdeckapp.utils

import com.example.recdeckapp.R
import com.example.recdeckapp.data.entities.EventType


fun EventType.shouldShowStatus(): Boolean {
    return this == EventType.JOINED_EVENTS || this == EventType.CANCELED_EVENTS
}

fun EventType.statusText(): String {
    return when (this) {
        EventType.JOINED_EVENTS -> "Attended"
        EventType.CANCELED_EVENTS -> "Event Cancel"
        else -> ""
    }
}

fun EventType.statusTextColor(): Int {
    return when (this) {
        EventType.JOINED_EVENTS -> R.color.tick_selected
        EventType.CANCELED_EVENTS -> R.color.red
        else -> android.R.color.transparent
    }
}

fun EventType.statusIcon(): Int {
    return when (this) {
        EventType.JOINED_EVENTS -> R.drawable.ic_tick_circle_unbackground
        EventType.CANCELED_EVENTS -> R.drawable.ic_cancel_circle
        else -> 0
    }
}