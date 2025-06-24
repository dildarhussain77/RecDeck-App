package com.example.recdeckapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recdeckapp.data.entities.NotificationModel
import com.example.recdeckapp.databinding.NotificationItemBinding

class NotificationAdapter(
    private val notificationList: List<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    inner class NotificationViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationModel) {
            binding.tvTitle.text = notification.title
            binding.tvDescription.text = notification.description
            binding.tvDateTime.text = notification.dateTime
            // If you want to load image from URL, use Glide/Picasso later
            binding.ivNotification.setBackgroundResource(notification.imageUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int = notificationList.size
}
