package com.example.recdeckapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

object DropdownUtils {
    fun setupDropdown(
        context: Context,
        anchorView: AutoCompleteTextView,
        items: List<String>,
        @LayoutRes itemLayoutRes: Int,
        @IdRes textViewId: Int,
        dropdownBgColorRes: Int = android.R.color.white, // default white background
        onItemSelected: (String) -> Unit = {}
    ) {
        val adapter = object : ArrayAdapter<String>(
            context,
            itemLayoutRes,
            textViewId,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(itemLayoutRes, parent, false)
                val itemText = getItem(position)
                view.findViewById<TextView>(textViewId).text = itemText
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return getView(position, convertView, parent)
            }
        }
        anchorView.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(dropdownBgColorRes)
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showDropDown()
            }
            setOnClickListener {
                adapter.filter.filter(null)
                showDropDown()
            }
            setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                onItemSelected(selectedItem)
            }
        }
    }
}
