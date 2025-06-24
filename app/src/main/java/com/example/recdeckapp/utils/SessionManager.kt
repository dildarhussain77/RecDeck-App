package com.example.recdeckapp.utils

import android.content.Context
import com.example.recdeckapp.data.roomDatabase.entities.SignUp.UserEntity

object SessionManager {
    private const val PREF_NAME = "MyPrefs"
    private const val KEY_USER_ID = "loggedInUserId"
    private const val KEY_EMAIL = "loggedInEmail"
    private const val PREF_ROLE = "loggedInUserRole"
    fun saveUser(context: Context, user: UserEntity) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_USER_ID, user.userId)
            .putString(KEY_EMAIL, user.email)
            .putString(PREF_ROLE, user.role)
            .apply()
    }

    fun getUserId(context: Context): Int {
        return context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            .getInt(KEY_USER_ID, -1)
    }

    fun getUserRole(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(PREF_ROLE, null)
    }

    //    fun getUserEmail(context: Context): String? {
//        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//            .getString(KEY_EMAIL, null)
//    }
    fun clearSession(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
