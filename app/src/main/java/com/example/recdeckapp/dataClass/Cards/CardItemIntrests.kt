package com.example.recdeckapp.dataClass.Cards

data class CardItemIntrests(
    val image: Int,
    val title: String,
    var isSelected: Boolean = false   // Track selection state
)
