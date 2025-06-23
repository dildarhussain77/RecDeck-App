package com.example.recdeckapp.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recdeckapp.databinding.DialogCustomDatePickerBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CustomDatePickerDialog {

    fun show(
        context: Context,
        initialDate: Long = System.currentTimeMillis(),
        onDateSelected: (formattedDate: String, rawDate: Long) -> Unit
    ) {
        val dialog = Dialog(context)
        val binding = DialogCustomDatePickerBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.92).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val currentCalendar = Calendar.getInstance().apply { timeInMillis = initialDate }
        val displayCalendar = Calendar.getInstance().apply { timeInMillis = initialDate }
        var selectedDate = initialDate

        // Setup RecyclerView
        val adapter = CalendarAdapter(emptyList()) { calendarDate ->
            selectedDate = calendarDate.calendar.timeInMillis
        }

        binding.calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        binding.calendarRecyclerView.adapter = adapter

        // Update calendar display
        fun updateCalendar() {
            val monthFormat = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
            binding.tvCurrentMonth.text = monthFormat.format(displayCalendar.time)

            val dates = generateCalendarDates(displayCalendar)
            adapter.updateDates(dates)

            // Set selected date if it's in current month
            if (displayCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                displayCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
            ) {
                adapter.setSelectedDate(currentCalendar)
            }
        }

        // Initial setup
        updateCalendar()

        // Navigation listeners
        binding.btnPrevMonth.setOnClickListener {
            displayCalendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        binding.btnNextMonth.setOnClickListener {
            displayCalendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        binding.ivCross.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnSelectDate.setOnClickListener {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formatted = sdf.format(Date(selectedDate))
            onDateSelected(formatted, selectedDate)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun generateCalendarDates(calendar: Calendar): List<CalendarAdapter.CalendarDate> {
        val dates = mutableListOf<CalendarAdapter.CalendarDate>()
        val tempCalendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        // Set to first day of month
        tempCalendar.time = calendar.time
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)

        // Get first day of week for this month
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1

        // Add empty cells for previous month
        repeat(firstDayOfWeek) {
            dates.add(CalendarAdapter.CalendarDate(0, false, Calendar.getInstance()))
        }

        // Get max days in current month
        val maxDaysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Add days of current month
        for (day in 1..maxDaysInMonth) {
            tempCalendar.set(Calendar.DAY_OF_MONTH, day)
            val isToday = tempCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    tempCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    tempCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)

            //val isPast = tempCalendar.before(today) // disables past including today
            //if today also exclude
            val isPast = tempCalendar.before(today.apply {
                set(Calendar.HOUR_OF_DAY, 0); set(
                Calendar.MINUTE,
                0
            ); set(Calendar.SECOND, 0)
            })


            dates.add(
                CalendarAdapter.CalendarDate(
                    day = day,
                    isCurrentMonth = true,
                    calendar = tempCalendar.clone() as Calendar,
                    isToday = isToday,
                    isPast = isPast
                )
            )
        }


        // Fill remaining cells to complete the grid (42 cells total for 6 rows)
        val remainingCells = 42 - dates.size
        repeat(remainingCells) {
            dates.add(CalendarAdapter.CalendarDate(0, false, Calendar.getInstance()))
        }

        return dates
    }
}