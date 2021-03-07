package com.pd.beertimer.feature.drinks

import com.pd.beertimer.room.Drink
import com.pd.beertimer.room.DrinkDao
import kotlinx.coroutines.flow.Flow

class DrinkRepository(private val drinkDb: DrinkDao) {
    fun getDrinks(): Flow<List<Drink>> {
        return drinkDb.getAllDrinks()
    }
}
