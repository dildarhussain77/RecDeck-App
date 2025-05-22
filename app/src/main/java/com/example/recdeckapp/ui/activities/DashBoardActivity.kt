package com.example.recdeckapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityDashBoardBinding
import com.example.recdeckapp.ui.fragments.dashBoard.FragmentBookings
import com.example.recdeckapp.ui.fragments.dashBoard.FragmentChats
import com.example.recdeckapp.ui.fragments.dashBoard.FragmentEvents
import com.example.recdeckapp.ui.fragments.dashBoard.FragmentGroups
import com.example.recdeckapp.ui.fragments.dashBoard.FragmentPitches
import com.example.recdeckapp.utils.StatusBarUtils

class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtils.setLightStatusBar(this, R.color.bg_grey)

        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // Load the first fragment when activity is created
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.dashBoardFragmentContainer.id, FragmentEvents())
                .commit()
        }

    }

    private fun setupBottomNavigation() {
        val defaultColor = ContextCompat.getColor(this, R.color.black)
        val selectedColor = ContextCompat.getColor(this, R.color.primary_main) // Change to your desired selected color

        // Map of layout id to Pair of ImageView & TextView
        val navItems = listOf(
            Triple(binding.clEvents, binding.ivEvents, binding.tvEvents),
            Triple(binding.clMyBookings, binding.ivMyBookings, binding.tvMyBookings),
            Triple(binding.clPitches, binding.ivPitches, binding.tvPitches),
            Triple(binding.clChats, binding.ivChats, binding.tvChats),
            Triple(binding.clGroups, binding.ivGroups, binding.tvGroups)
        )

        // Map icons (default & selected)
        val iconsMap = mapOf(
            R.id.clEvents to Pair(R.drawable.ic_events, R.drawable.ic_events_selected),
            R.id.clMyBookings to Pair(R.drawable.ic_calender_booking, R.drawable.ic_calender_booking_selected),
            R.id.clPitches to Pair(R.drawable.ic_pitches, R.drawable.ic_pitches_selected),
            R.id.clChats to Pair(R.drawable.ic_chat, R.drawable.ic_chats_selected),
            R.id.clGroups to Pair(R.drawable.ic_group, R.drawable.ic_group_selected)
        )

        fun selectItem(selectedId: Int) {
            navItems.forEach { (layout, iconView, textView) ->
                val isSelected = layout.id == selectedId
                val iconPair = iconsMap[layout.id]
                iconView.setImageResource(if (isSelected) iconPair?.second ?: 0 else iconPair?.first ?: 0)
                textView.setTextColor(if (isSelected) selectedColor else defaultColor)
            }
        }

        // Initial selection
        selectItem(R.id.clEvents)

        // Set up click listeners
        navItems.forEach { (layout, _, _) ->
            layout.setOnClickListener {
                selectItem(layout.id)

                val fragment = when (layout.id) {
                    R.id.clEvents -> FragmentEvents()
                    R.id.clMyBookings -> FragmentBookings()
                    R.id.clPitches -> FragmentPitches()
                    R.id.clChats -> FragmentChats()
                    R.id.clGroups -> FragmentGroups()
                    else -> null
                }

                fragment?.let {
                    switchFragment(it)
                }
            }
        }

    }


    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.dashBoardFragmentContainer.id, fragment)
            .addToBackStack(null)  // To handle back navigation
            .commit()
    }

}