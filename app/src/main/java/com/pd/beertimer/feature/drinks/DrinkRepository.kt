package com.pd.beertimer.feature.drinks

import com.pd.beertimer.room.Drink
import com.pd.beertimer.room.DrinkDao

class DrinkRepository(private val drinkDb: DrinkDao) {
    fun getDrinks(): List<Drink> {
        return drinkDb.getAllDrinks()
    }
}
