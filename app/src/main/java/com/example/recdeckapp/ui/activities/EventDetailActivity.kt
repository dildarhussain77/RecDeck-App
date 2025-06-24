package com.example.recdeckapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityEventDetailBinding
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.StatusBarUtils
import com.example.recdeckapp.viewmodel.EventCreationViewModel

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding

    private lateinit var eventCreationViewModel: EventCreationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventCreationViewModel =
            ViewModelProvider(this)[EventCreationViewModel::class.java]

        StatusBarUtils.setLightStatusBar(this, R.color.bg_grey)

        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
        //bindPitchData(eventI)
    }


    private fun setOnClickListener() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvEventCancel.setOnClickListener {
            AlertDialogUtils.showCancelDialog(
                this,
                message = "Are you sure you want to delete this event?",
                onYesClicked = {
                    finish()
                },
            )
        }
    }

//    private fun bindPitchData(event: EventEntity) {
//        binding.tvEventName.text = event.eventName
//        binding.tvEventDetail.text = event.eventDescription
//
//        // If you’re using Glide for image loading:
//        Glide.with(requireContext())
//            .load(event.eventImageUrl)
//            .placeholder(R.drawable.ic_image)
//            .into(binding.ivEventImage)
//
//    }
}