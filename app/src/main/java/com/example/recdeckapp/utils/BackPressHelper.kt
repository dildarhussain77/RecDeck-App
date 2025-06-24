package com.example.recdeckapp.utils

import android.view.View
import androidx.fragment.app.Fragment

object BackPressHelper {
    fun handleBackPress(
        fragment: Fragment,
        backArrowView: View
    ) {
        backArrowView.bringToFront()
        backArrowView.setOnClickListener {
            fragment.requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}