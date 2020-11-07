package com.pd.beertimer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DrinkDao {
    @Query("SELECT * FROM drink")
    fun getAllDrinks(): List<Drink>

    @Insert
    fun insertAll(vararg users: Drink)
}