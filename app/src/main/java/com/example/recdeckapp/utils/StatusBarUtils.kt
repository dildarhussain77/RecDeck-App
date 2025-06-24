package com.example.recdeckapp.utils

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat

object StatusBarUtils {
    fun setLightStatusBar(activity: Activity, colorResId: Int) {
        val window = activity.window
        window.statusBarColor = ContextCompat.getColor(activity, colorResId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Light status bar = dark icons (for light backgrounds)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun setDarkStatusBar(activity: Activity, colorResId: Int) {
        val window = activity.window
        window.statusBarColor = ContextCompat.getColor(activity, colorResId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Reset to default (light icons)
            window.decorView.systemUiVisibility = 0
        }
    }
}
