package com.example.recdeckapp.data.entities

data class PitchInfo(
    val pitchId: Int = 0,
    val name: String,
    val price: String,
    val location: String,
    val time: String,
    val isPitchAvailable: Boolean,
    val imageResId: Int
)
