package com.example.recdeckapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.example.recdeckapp.R
import com.example.recdeckapp.data.entities.City

class CityDropdownHelper(
    private val context: Context,
    private val cityAutoComplete: AutoCompleteTextView
) {
    private var currentAdapter: ArrayAdapter<City>? = null
    fun updateCities(cities: List<String>) {
        val cityList = cities.map { City(it) }
        val adapter = object : ArrayAdapter<City>(
            context, R.layout.city_dropdown, R.id.cityNameTextView, cityList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.city_dropdown, parent, false)
                view.findViewById<TextView>(R.id.cityNameTextView).text = getItem(position)?.name
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
        cityAutoComplete.setAdapter(adapter)
        cityAutoComplete.setOnClickListener { cityAutoComplete.showDropDown() }
        currentAdapter = adapter
    }
}
