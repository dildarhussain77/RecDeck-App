package com.example.recdeckapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.NotificationAdapter
import com.example.recdeckapp.data.entities.NotificationModel
import com.example.recdeckapp.databinding.ActivityNotificationBinding
import com.example.recdeckapp.utils.StatusBarUtils

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setLightStatusBar(this, R.color.white_light)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
        setupRecyclerView()
    }

    private fun setOnClickListener() {
        binding.ivBackNotification.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val dummyNotifications = listOf(
            NotificationModel(
                imageUrl = R.drawable.ic_calender_notification,
                title = "Event Notification . . . .",
                description = "Lorem ipsum dolor sit amet consectetur. Nulla ipsum quam placerat purus sapien pulvinar curabitur.",
                dateTime = "Just Now"
            ),
            NotificationModel(
                imageUrl = R.drawable.ic_group_notification,
                title = "New Message",
                description = "Lorem ipsum dolor sit amet consectetur. Nulla ipsum quam placerat purus sapien pulvinar curabitur.",
                dateTime = "11 min ago"
            ),
            NotificationModel(
                imageUrl = R.drawable.ic_done_notification,
                title = "Event Reminder",
                description = "Lorem ipsum dolor sit amet consectetur. Nulla ipsum quam placerat purus sapien pulvinar curabitur..",
                dateTime = "Yesterday"
            ),
            NotificationModel(
                imageUrl = R.drawable.ic_chat_notification,
                title = "Event Reminder",
                description = "Lorem ipsum dolor sit amet consectetur. Nulla ipsum quam placerat purus sapien pulvinar curabitur.",
                dateTime = "1 day ago"
            ),
            NotificationModel(
                imageUrl = R.drawable.ic_done_notification,
                title = "Event Reminder",
                description = "Lorem ipsum dolor sit amet consectetur. Nulla ipsum quam placerat purus sapien pulvinar curabitur.",
                dateTime = "12 May, 2024"
            ),
            NotificationModel(
                imageUrl = R.drawable.ic_calender_notification,
                title = "Event Reminder",
                description = "Lorem ipsum dolor sit amet consectetur. Nulla ipsum quam placerat purus sapien pulvinar curabitur.",
                dateTime = "12 May, 2024"
            ),
            NotificationModel(
                imageUrl = R.drawable.ic_calender_notification,
                title = "Event Reminder",
                description = "Lorem ipsum dolor sit amet consectetur. Nulla ipsum quam placerat purus sapien pulvinar curabitur.",
                dateTime = "12 May, 2024"
            ),
        )
        val notificationAdapter = NotificationAdapter(dummyNotifications)
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(this@NotificationActivity)
            adapter = notificationAdapter
        }
    }
}
