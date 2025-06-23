package com.example.recdeckapp.utils

import android.app.TimePickerDialog
import android.content.Context
import android.widget.EditText
import com.example.recdeckapp.R
import com.example.recdeckapp.ui.activities.BaseActivity
import java.util.Calendar

object TimePickerUtils {

    fun attachTimePickers(
        context: Context,
        startEditText: EditText,
        endEditText: EditText,
        onTimesUpdated: (startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) -> Unit
    ) {
        var startHour = -1
        var startMinute = -1
        var endHour = -1
        var endMinute = -1

        fun toMinutes(h: Int, m: Int) = if (h == -1) -1 else h * 60 + m

        startEditText.setOnClickListener {
            showTimePicker(
                context,
                true,
                startHour,
                startMinute,
                endHour,
                endMinute
            ) { hour, minute, formatted ->
                val newStart = toMinutes(hour, minute)
                val currentEnd = toMinutes(endHour, endMinute)

                if (endHour == -1 || newStart <= currentEnd - 60) {
                    startHour = hour
                    startMinute = minute
                    startEditText.setText(formatted)

                    // Clear invalid end
                    if (endHour != -1 && newStart > currentEnd - 60) {
                        endHour = -1
                        endMinute = -1
                        endEditText.setText("")
                    }

                    onTimesUpdated(startHour, startMinute, endHour, endMinute)
                } else {
                    (context as? BaseActivity)?.showToast("Start time must be at least 1 hour before end time")
                }
            }
        }

        endEditText.setOnClickListener {
            showTimePicker(
                context,
                false,
                startHour,
                startMinute,
                endHour,
                endMinute
            ) { hour, minute, formatted ->
                val newEnd = toMinutes(hour, minute)
                val currentStart = toMinutes(startHour, startMinute)

                if (startHour == -1 || newEnd >= currentStart + 60) {
                    endHour = hour
                    endMinute = minute
                    endEditText.setText(formatted)

                    // Clear invalid start
                    if (startHour != -1 && newEnd < currentStart + 60) {
                        startHour = -1
                        startMinute = -1
                        startEditText.setText("")
                    }

                    onTimesUpdated(startHour, startMinute, endHour, endMinute)
                } else {
                    (context as? BaseActivity)?.showToast("End time must be at least 1 hour after start time")
                }
            }
        }
    }

    private fun showTimePicker(
        context: Context,
        isStartTime: Boolean,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        onTimeSelected: (Int, Int, String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        var initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        var initialMinute = calendar.get(Calendar.MINUTE)

        if (isStartTime && endHour != -1) {
            val maxStart = endHour * 60 + endMinute - 60
            initialHour = maxStart / 60
            initialMinute = maxStart % 60
        } else if (!isStartTime && startHour != -1) {
            val minEnd = startHour * 60 + startMinute + 60
            initialHour = minEnd / 60
            initialMinute = minEnd % 60
        }

        TimePickerDialog(
            context,
            R.style.CustomDatePickerDialog,
            { _, selectedHour, selectedMinute ->
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val hourIn12 = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                val formatted = String.format("%02d:%02d %s", hourIn12, selectedMinute, amPm)
                onTimeSelected(selectedHour, selectedMinute, formatted)
            },
            initialHour,
            initialMinute,
            false
        ).show()
    }
}
