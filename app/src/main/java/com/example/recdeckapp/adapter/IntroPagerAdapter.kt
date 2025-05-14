package com.example.recdeckapp.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.recdeckapp.R
import com.example.recdeckapp.ui.fragments.IntroFragments.IntroFragment1
import com.example.recdeckapp.ui.fragments.IntroFragments.IntroFragment2
import com.example.recdeckapp.ui.fragments.IntroFragments.IntroFragment3


class IntroPagerAdapter(fa: FragmentActivity, private val context: Context) :
    FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IntroFragment1.newInstance(
                R.drawable.event_frames,
            )

            1 -> IntroFragment2.newInstance(
                R.drawable.profiles_frames,
            )

            2 -> IntroFragment3.newInstance(
                R.drawable.chats_frames,

                )

            else -> IntroFragment1.newInstance(

                R.drawable.event_frames,

                )
        }
    }
}