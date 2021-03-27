package com.pd.beertimer.models

import com.pd.beertimer.room.Drink
import kotlinx.serialization.Serializable

@Serializable
data class AlcoholUnit(
    val name: String,
    val volume: Float,
    val percentage: Float,
    val iconName: String,
    var isSelected: Boolean = false
) {
    val gramPerUnit = volume * 1000 * percentage * 0.789

    fun toDrinkEntity(): Drink {
        return Drink(
            name = name,
            volume = volume,
            percentage = percentage,
            iconName = iconName
        )
    }
}
