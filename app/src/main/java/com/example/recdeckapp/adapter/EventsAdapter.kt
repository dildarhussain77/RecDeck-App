package com.example.recdeckapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recdeckapp.R
import com.example.recdeckapp.data.entities.EventType
import com.example.recdeckapp.databinding.ItemEventsBinding

class EventsAdapter(

    private val items: List<EventType>,
    private val onItemClick: (EventType) -> Unit,

) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    private var selectedItem = 0 // Initially select first item

    inner class ViewHolder(private val binding: ItemEventsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventType) {
            binding.tvEventName.text = item.displayName
            if (position == selectedItem) {
                binding.tvEventName.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context, R.color.white
                    )
                )

                val color = ContextCompat.getColor(itemView.context, R.color.primary_main)
                binding.cvPromo.setCardBackgroundColor(color)

            } else {

                binding.tvEventName.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context, R.color.black
                    )
                )
                val color = ContextCompat.getColor(itemView.context, R.color.white)
                binding.cvPromo.setCardBackgroundColor(color)


            }
            binding.root.setOnClickListener {
                selectedItem = position
                onItemClick(item)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}