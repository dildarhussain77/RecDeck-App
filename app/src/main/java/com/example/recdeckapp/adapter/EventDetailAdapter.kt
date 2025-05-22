package com.example.recdeckapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recdeckapp.data.entities.Event
import com.example.recdeckapp.databinding.ItemEventDetailBinding

class EventDetailAdapter (private val onImageClick: () -> Unit)
    : ListAdapter<Event, EventDetailAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemEventDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) = with(binding) {
            ivEventImage.setImageResource(event.imageResId)
            tvEventName.text = event.name
            tvEventDate.text = "${event.date}, ${event.time}"
            tvEventPrice.text = event.price
            tvAttendeesStrength.text = "${event.totalAttendees} Attending"
            ivEventImage.setOnClickListener{
                onImageClick()
            }

            val imageViews = listOf(ivAttendeeOne, ivAttendeeTwo, ivAttendeeThree, ivAttendeeFour)
            imageViews.forEach { it.visibility = View.GONE }

            event.attendees.take(4).forEachIndexed { index, attendee ->
                imageViews[index].visibility = View.VISIBLE
                imageViews[index].setImageResource(attendee.drawableResId)
            }

            


        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(old: Event, new: Event) = old.name == new.name
        override fun areContentsTheSame(old: Event, new: Event) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemEventDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
