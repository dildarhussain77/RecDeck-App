package com.example.recdeckapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.recdeckapp.R
import com.example.recdeckapp.ui.fragments.fragmentIntro.IntroFragment1
import com.example.recdeckapp.ui.fragments.fragmentIntro.IntroFragment2
import com.example.recdeckapp.ui.fragments.fragmentIntro.IntroFragment3

class IntroPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IntroFragment1.newInstance(
                R.drawable.profile,
                "Lorem ipsum dolor sit amet consectetur.",
                "Lorem ipsum dolor sit amet consectetur. In eros neque ac pharetra cursus duis pellentesque faucibus. Massa lacus posuere malesuada lorem pretium. Augue ut mollis quis arcu. Mi ac aliquam non adipiscing magna.",
            )

            1 -> IntroFragment2.newInstance(
                R.drawable.profile,
                "Lorem ipsum dolor sit amet consectetur.",
                "Lorem ipsum dolor sit amet consectetur. In eros neque ac pharetra cursus duis pellentesque faucibus. Massa lacus posuere malesuada lorem pretium. Augue ut mollis quis arcu. Mi ac aliquam non adipiscing magna."
            )

            2 -> IntroFragment3.newInstance(
                R.drawable.profile,
                R.drawable.profile,
                R.drawable.profile,
                "Lorem ipsum dolor sit amet consectetur.",
                "Lorem ipsum dolor sit amet consectetur. In eros neque ac pharetra cursus duis pellentesque faucibus. Massa lacus posuere malesuada lorem pretium. Augue ut mollis quis arcu. Mi ac aliquam non adipiscing magna."
            )
            else -> IntroFragment1.newInstance(
                R.drawable.profile,
                "Lorem ipsum dolor sit amet consectetur.",
                "Lorem ipsum dolor sit amet consectetur. In eros neque ac pharetra cursus duis pellentesque faucibus. Massa lacus posuere malesuada lorem pretium. Augue ut mollis quis arcu. Mi ac aliquam non adipiscing magna."
            )
        }
    }
}