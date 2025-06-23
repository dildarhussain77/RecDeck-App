package com.example.recdeckapp.utils

import com.example.recdeckapp.R
import com.example.recdeckapp.data.entities.CardItemIntrests
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity

object InterestItemsProvider {

    fun getDefaultInterestItems(): List<CardItemIntrests> = listOf(
        CardItemIntrests(1, R.drawable.basket_ball, "BasketBall"),
        CardItemIntrests(2, R.drawable.table_tennis, "Table Tennis"),
        CardItemIntrests(3, R.drawable.horse_ride, "Horse Ride"),
        CardItemIntrests(4, R.drawable.squash, "Squash"),
        CardItemIntrests(5, R.drawable.cricket, "Cricket"),
        CardItemIntrests(6, R.drawable.hockey, "Hockey"),
        CardItemIntrests(7, R.drawable.cycling, "Cycling"),
        CardItemIntrests(8, R.drawable.football, "Football"),
        CardItemIntrests(9, R.drawable.golf, "Golf"),
        CardItemIntrests(10, R.drawable.swimming_pool, "Swimming pool"),
    )

    fun getDefaultInterestItemsAppDatabase(): List<InterestEntity> = listOf(
        InterestEntity(1, "BasketBall", R.drawable.basket_ball),
        InterestEntity(2, "Table Tennis", R.drawable.table_tennis),
        InterestEntity(3, "Horse Ride", R.drawable.horse_ride),
        InterestEntity(4, "Squash", R.drawable.squash),
        InterestEntity(5, "Cricket", R.drawable.cricket),
        InterestEntity(6, "Hockey", R.drawable.hockey),
        InterestEntity(7, "Cycling", R.drawable.cycling),
        InterestEntity(8, "Football", R.drawable.football),
        InterestEntity(9, "Golf", R.drawable.golf),
        InterestEntity(10, "Swimming pool", R.drawable.swimming_pool)
    )



}