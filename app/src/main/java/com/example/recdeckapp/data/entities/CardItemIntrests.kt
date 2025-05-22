package com.example.recdeckapp.data.entities

data class CardItemIntrests(
    val image: Int,
    val title: String,
    var isSelected: Boolean = false   // Track selection state
)
