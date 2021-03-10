package com.pd.beertimer.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pd.beertimer.models.AlcoholUnit
import com.pd.beertimer.models.MyDrinkItem


@Entity
data class Drink(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "drink_name") val name: String,
    @ColumnInfo(name = "drink_volume") val volume: Float,
    @ColumnInfo(name = "drink_percentage") val percentage: Float,
    @ColumnInfo(name = "drink_icon_res_id") val iconName: String
) {
    constructor(name: String, volume: Float, percentage: Float, iconName: String):
            this(id = 0, name = name, volume = volume, percentage = percentage, iconName = iconName)
    fun toAlcoholUnit(): AlcoholUnit {
        return AlcoholUnit(
            name=name,
            volume = volume,
            percentage = percentage,
            iconName = iconName,
            isSelected = false
        )
    }

    fun toDrinkItem(): MyDrinkItem {
        return MyDrinkItem(
            name = name,
            volume = volume,
            percentage = percentage,
            iconName = iconName
        )
    }
}