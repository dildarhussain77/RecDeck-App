package com.example.recdeckapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ItemCalendarDateBinding
import java.util.Calendar

class CalendarAdapter(
    private var dates: List<CalendarDate>,
    private val onDateClick: (CalendarDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {

    private var selectedPosition = -1

    data class CalendarDate(
        val day: Int,
        val isCurrentMonth: Boolean,
        val calendar: Calendar,
        val isToday: Boolean = false,
        val isPast: Boolean = false
    )

    inner class DateViewHolder(private val binding: ItemCalendarDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: CalendarDate, position: Int) {
            binding.tvDate.text = if (date.day == 0) "" else date.day.toString()

            // Set visibility and clickability
            if (date.day == 0) {
                binding.tvDate.alpha = 0f
                binding.root.isClickable = false
                binding.tvDate.background = null
            } else {
                binding.root.isClickable = true
                binding.tvDate.isEnabled = !date.isPast

                // Reset background to ensure selector works
                binding.tvDate.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.bg_date_selector
                )

                // Set text color based on month and selection
                when {
                    position == selectedPosition -> {
                        binding.tvDate.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.white
                            )
                        )
                        binding.tvDate.isSelected = true
                    }

                    !date.isCurrentMonth -> {
                        binding.tvDate.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.calendar_other_month,
                            )
                        )
                        binding.tvDate.isSelected = false
                    }

                    date.isToday -> {
                        binding.tvDate.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.primary_main
                            )
                        )
                        binding.tvDate.isSelected = false
                    }

                    else -> {
                        binding.tvDate.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.black
                            )
                        )
                        binding.tvDate.isSelected = false
                    }
                }

                binding.tvDate.alpha = if (date.isCurrentMonth) 1f else 0.5f
            }

            binding.root.setOnClickListener {
                if (date.day != 0 && !date.isPast) {
                    val oldPosition = selectedPosition
                    selectedPosition = position

                    // Notify changes for smooth animation
                    if (oldPosition != -1) notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)

                    onDateClick(date)
                }
            }

            //itemView.isEnabled = !date.isPast
            itemView.alpha = if (date.isPast) 0.4f else 1.0f

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemCalendarDateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position], position)
    }

    override fun getItemCount(): Int = dates.size

    fun updateDates(newDates: List<CalendarDate>) {
        dates = newDates
        selectedPosition = -1
        notifyDataSetChanged()
    }

    fun setSelectedDate(calendar: Calendar) {
        val position = dates.indexOfFirst { date ->
            date.calendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                    date.calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    date.calendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                    date.isCurrentMonth
        }

        if (position != -1) {
            val oldPosition = selectedPosition
            selectedPosition = position

            if (oldPosition != -1) notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }
    }
}