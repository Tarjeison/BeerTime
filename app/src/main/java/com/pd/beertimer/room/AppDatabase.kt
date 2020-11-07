package com.pd.beertimer.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Drink::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drinkDao(): DrinkDao
}