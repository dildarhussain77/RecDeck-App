package com.example.recdeckapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityEventDetailBinding
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.StatusBarUtils

class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtils.setLightStatusBar(this, R.color.bg_grey)

        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListener()
    }


    private fun setOnClickListener(){

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
}