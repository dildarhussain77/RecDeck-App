package com.example.recdeckapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recdeckapp.R
import com.example.recdeckapp.data.roomDatabase.entities.EventCreation.EventEntity
import com.example.recdeckapp.databinding.ItemEventInfoBinding

class EventListAdapter(
    private val onItemClick: (EventEntity) -> Unit,
    private val onEditClick: (EventEntity) -> Unit,
    private val onDeleteClick: (EventEntity) -> Unit,

    ) :
    RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    private val eventList = mutableListOf<EventEntity>()

    fun submitList(newList: List<EventEntity>) {
        eventList.clear()
        eventList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeEvent(event: EventEntity): Int {
        val index = eventList.indexOfFirst { it.eventId == event.eventId }
        if (index != -1) {
            eventList.removeAt(index)
        }
        return index
    }


    inner class EventViewHolder(val binding: ItemEventInfoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            ItemEventInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        with(holder.binding) {
            tvEventName.text = event.eventName
            tvDescription.text = event.eventDescription
            Glide.with(ivEventImage.context)
                .load(event.eventImageUrl)
                .placeholder(R.drawable.ic_image)
                .into(ivEventImage)

            root.setOnClickListener {
                onItemClick(event)
            }
            tvEventEdit.setOnClickListener {
                onEditClick(event)
            }
            tvEventDelete.setOnClickListener {
                onDeleteClick(event)
            }
        }
    }

    override fun getItemCount() = eventList.size

}

