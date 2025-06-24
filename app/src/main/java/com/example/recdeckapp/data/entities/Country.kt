package com.example.recdeckapp.data.entities

// Data class to hold country information
data class Country(
    val name: String,
    val flagResId: Int, // Resource ID for the flag drawable
    val cities: List<String>
) {
    override fun toString(): String = name
}

data class City(val name: String) {
    override fun toString(): String = name
}