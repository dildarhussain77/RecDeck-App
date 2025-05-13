package com.example.recdeckapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.recdeckapp.databinding.SpinnerCountryItemBinding

class CountryAdapter(
    context: Context,
    private val countries: List<String>,
    private val flags: List<Int>,
) : ArrayAdapter<String>(context, 0, countries) {

    // Function to inflate and bind the layout
    private fun getBinding(convertView: View?, parent: ViewGroup): SpinnerCountryItemBinding {
        return if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            SpinnerCountryItemBinding.inflate(inflater, parent, false)
        } else {
            SpinnerCountryItemBinding.bind(convertView)
        }
    }

    // For displaying the selected item
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = getBinding(convertView, parent)
        binding.ivFlag.setImageResource(flags[position])
        binding.tvCountryName.text = countries[position]
        return binding.root
    }

    // For displaying dropdown items
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = getBinding(convertView, parent)
        binding.ivFlag.setImageResource(flags[position])
        binding.tvCountryName.text = countries[position]
        return binding.root
    }
}
