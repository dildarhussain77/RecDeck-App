package com.example.recdeckapp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.switchFragment(containerId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .addToBackStack(null)
        .commit()
}

fun FragmentActivity.loadInitialFragment(containerId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .commit()
}
