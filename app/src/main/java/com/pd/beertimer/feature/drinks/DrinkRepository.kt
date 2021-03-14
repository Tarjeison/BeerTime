package com.pd.beertimer.feature.drinks

import com.pd.beertimer.room.Drink
import com.pd.beertimer.room.DrinkDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DrinkRepository(private val drinkDb: DrinkDao) {
    fun getDrinks(): Flow<List<Drink>> {
        return drinkDb.getAllDrinks()
    }

    fun insert(drink: Drink): Flow<Long> {
        return flow {
            drinkDb.insertAll(drink)
            emit(1L)
        }.flowOn(Dispatchers.IO)
    }
}
