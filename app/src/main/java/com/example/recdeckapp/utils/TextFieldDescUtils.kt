package com.example.recdeckapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView

object TextFieldDescUtils {

    fun setupDescWatcher(
        editText: EditText,
        charCountTextView: TextView,
        maxLength: Int = 600,
        onValidChanged: ((Boolean) -> Unit)? = null
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                charCountTextView.text = "$currentLength/$maxLength " + "Characters"

                val isValid = currentLength in 1..maxLength
                onValidChanged?.invoke(isValid)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
