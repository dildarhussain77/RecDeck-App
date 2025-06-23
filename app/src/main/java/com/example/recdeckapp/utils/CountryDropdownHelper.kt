//package com.example.recdeckapp.utils
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import com.example.recdeckapp.R
//import com.example.recdeckapp.data.entities.Country
//import com.example.recdeckapp.databinding.FragmentSignupBasicInfoBinding
//
//class CountryDropdownHelper(
//    private val context: Context,
//    private val binding: FragmentSignupBasicInfoBinding,
//    private val onCountrySelected: (Country) -> Unit
//) {
//
//    private val countries = listOf(
//        Country("UAE", R.drawable.uae, listOf("Abu Dhabi", "Dubai", "Sharjah", "Ajman")),
//        Country("USA", R.drawable.usa, listOf("New York", "Los Angeles", "Chicago")),
//        Country("Pak", R.drawable.pakistan, listOf("Islamabad", "Lahore", "Karachi")),
//        Country("India", R.drawable.india, listOf("Mumbai", "Delhi", "Bangalore"))
//    )
//
//    fun setupDropdown() {
//        val adapter = object : ArrayAdapter<Country>(
//            context, R.layout.country_dropdown_item, R.id.countryNameTextView, countries
//        ) {
//            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//                val view = convertView ?: LayoutInflater.from(context)
//                    .inflate(R.layout.country_dropdown_item, parent, false)
//                val country = getItem(position) ?: return view
//                view.findViewById<ImageView>(R.id.flagImageView).setImageResource(country.flagResId)
//                view.findViewById<TextView>(R.id.countryNameTextView).text = country.name
//                return view
//            }
//
//            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//                return getView(position, convertView, parent)
//            }
//        }
//
//        binding.countryAutoComplete.setAdapter(adapter)
//        binding.countryAutoComplete.setOnClickListener { binding.countryAutoComplete.showDropDown() }
//
//        binding.countryAutoComplete.setOnItemClickListener { _, _, position, _ ->
//            val selected = adapter.getItem(position) ?: return@setOnItemClickListener
//            val flag = getCircularDrawable(context, selected.flagResId, 60, 14)
//
//            binding.countryAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
//                flag, null,
//                ContextCompat.getDrawable(context, R.drawable.ic_arrow_down),
//                null
//            )
//            binding.countryAutoComplete.setText(selected.name, false)
//            onCountrySelected(selected)
//        }
//    }
//}
