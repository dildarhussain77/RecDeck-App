package com.example.recdeckapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recdeckapp.R
import com.example.recdeckapp.data.entities.Event
import com.example.recdeckapp.data.entities.EventType
import com.example.recdeckapp.databinding.ItemEventDetailBinding
import com.example.recdeckapp.utils.shouldShowStatus
import com.example.recdeckapp.utils.statusIcon
import com.example.recdeckapp.utils.statusText
import com.example.recdeckapp.utils.statusTextColor

class EventDetailAdapter(
    private val eventType: EventType, private val onEventImageClick: (Event) -> Unit
) :

    ListAdapter<Event, EventDetailAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemEventDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(event: Event) = with(binding) {
            Glide.with(itemView.context).load(event.imageResId).skipMemoryCache(false)
                .into(ivEventImage)

            tvEventName.text = event.name
            tvEventDate.text = "${event.date}, ${event.time}"
            tvEventPrice.text = event.price
            tvAttendeesStrength.text = "${event.totalAttendees} Attending"

            val imageViews = listOf(ivAttendeeOne, ivAttendeeTwo, ivAttendeeThree, ivAttendeeFour)
            imageViews.forEach { it.visibility = View.GONE }
            event.attendees.take(4).forEachIndexed { index, attendee ->
                imageViews[index].visibility = View.VISIBLE
                Glide.with(imageViews[index].context).load(attendee.drawableResId).circleCrop()
                    .into(imageViews[index])
            }
            //showing image according to event saved or not
            ivSaveEvent.bringToFront()
            icSaveEvent.setImageResource(
                if (eventType == EventType.SAVED_EVENTS) R.drawable.ic_saved
                else R.drawable.ic_un_saved

            )
            // Use extension functions to handle status
            if (eventType.shouldShowStatus()) {
                tvEventStatus.apply {
                    visibility = View.VISIBLE
                    text = eventType.statusText()
                    setTextColor(ContextCompat.getColor(context, eventType.statusTextColor()))
                    setCompoundDrawablesWithIntrinsicBounds(eventType.statusIcon(), 0, 0, 0)
                }
            } else {
                tvEventStatus.visibility = View.GONE
            }


            ///moving to event detail activity
            ivEventImage.setOnClickListener {
                onEventImageClick(event)
            }

        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(old: Event, new: Event) = old.name == new.name
        override fun areContentsTheSame(old: Event, new: Event) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemEventDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}