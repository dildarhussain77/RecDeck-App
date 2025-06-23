package com.example.recdeckapp.data.entities

data class CardItemIntrests(
    val id: Int,
    val imageResource: Int,
    val title: String,
    var isSelected: Boolean = false   // Track selection state
)
