package com.example.beertime.models

data class StartDrinkningInfo(
    val wantedBloodLevel: Float,
    val plannedHoursDrinking: Int,
    val alcoholType: AlcoholUnit
)