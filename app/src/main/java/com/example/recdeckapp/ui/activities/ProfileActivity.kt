package com.example.recdeckapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityProfileBinding
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.utils.StatusBarUtils
import com.example.recdeckapp.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewModel
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        StatusBarUtils.setLightStatusBar(this, R.color.bg_grey)
        setOnClickListener()
        setupBackPressHandler()
        getUserData()
    }

    private fun setOnClickListener() {
        binding.ivBackProfile.setOnClickListener {
            finish()
        }
        binding.tvLogOut.setOnClickListener {

            AlertDialogUtils.showCancelDialog(
                this,
                title = "Logout?",
                message = "Are you sure you want to logout \n",
                onYesClicked = {
                    SessionManager.clearSession(this) // Clean everything
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    this.finish()
                },
            )

        }
        binding.clBecomeFacility.setOnClickListener {
            val intent = Intent(this@ProfileActivity, PitchCreationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getUserData() {
        val userId = SessionManager.getUserId(this)

        //Fetch from Room
        profileViewModel.getUserData(userId)

        //Observe and bind data to views
        profileViewModel.userLiveData.observe(this) { user ->
            binding.tvProfileUserName.text = user.fullName
            binding.tvProfileUserRole.text = user.role

            Glide.with(this)
                .load(user.profilePicPath) // handle null with placeholder if needed
                .placeholder(R.drawable.img_hockey)
                .into(binding.ivProfilePicture)
        }
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }
}