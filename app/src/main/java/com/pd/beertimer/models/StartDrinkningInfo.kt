package com.pd.beertimer.models

data class StartDrinkningInfo(
    val wantedBloodLevel: Float,
    val plannedHoursDrinking: Int,
    val alcoholType: AlcoholUnit
)