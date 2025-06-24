package com.example.recdeckapp.utils

import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import com.example.recdeckapp.R

class PasswordToggleHelper(
    private val passwordEditText: EditText,
    private val toggleImageView: ImageView
) {
    private var isPasswordVisible = false
    fun toggle() {
        if (isPasswordVisible) {
            passwordEditText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleImageView.setImageResource(R.drawable.ic_visibility)
        } else {
            passwordEditText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleImageView.setImageResource(R.drawable.ic_visibility_off)
        }
        isPasswordVisible = !isPasswordVisible
        // Keep cursor at the end like a smooth operator
        passwordEditText.text?.let {
            passwordEditText.setSelection(it.length)
        }
    }
}
