package com.pd.beertimer.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.pd.beertimer.NotificationBroadcast
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmUtils(context: Context) : ContextWrapper(context) {

    fun setAlarmsAndStoreTimesToSharedPref(
        localDateTimes: List<LocalDateTime>,
        calculator: DrinkingCalculator
    ) {
        saveDrinkingValuesToSharedPref(localDateTimes, calculator)
        val alarmTimes = localDateTimes.slice(1..localDateTimes.lastIndex)
        val aManagers =
            List(alarmTimes.size) { baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
        for (i in aManagers.indices) {
            val millisTriggerTime =
                alarmTimes[i].atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val alarmIntent = Intent(baseContext, NotificationBroadcast::class.java).let { intent ->
                intent.putExtra("NOT", "WOOOW")
                PendingIntent.getBroadcast(baseContext, millisTriggerTime.toInt(), intent, 0)
            }

            aManagers[i].setExact(
                AlarmManager.RTC_WAKEUP,
                alarmTimes[i].atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                alarmIntent
            )
        }
    }

    fun deleteExistingAlarms(): Boolean {
        val localDateTimes = getExistingDrinkTimesFromSharedPref() ?: return false
        val aManagers =
            List(localDateTimes.size) { baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
        val now = LocalDateTime.now()
        for (i in aManagers.indices) {
            if (localDateTimes[i] > now) {
                val millisTriggerTime =
                    localDateTimes[i].atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val alarmIntent =
                    Intent(applicationContext, NotificationBroadcast::class.java).let { intent ->
                        PendingIntent.getBroadcast(
                            baseContext,
                            millisTriggerTime.toInt(),
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                        )
                    }
                aManagers[i].cancel(alarmIntent)
            }
        }
        clearDrinkingValuesSharedPref()
        return true
    }

    fun getExistingDrinkTimesFromSharedPref(): List<LocalDateTime>? {
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        sharedPref.getString(SHARED_PREF_DRINKING_TIMES, null)?.let {
            var drinkingTimesString = it.trim('[', ']')
            if (drinkingTimesString.isEmpty()) return null
            drinkingTimesString = drinkingTimesString.replace(" ", "")
            val drinkingTimesStringArray = drinkingTimesString.split(",")
            val drinkingLocalDateTimes = drinkingTimesStringArray.map { drinkingTimeString ->
                LocalDateTime.parse(drinkingTimeString)
            }
            if (drinkingLocalDateTimes.last() > LocalDateTime.now()) {
                return drinkingLocalDateTimes
            }
        }
        return null
    }

//    fun getCurrentlyDrinkingAlcoholUnitSharedPref(): AlcoholUnit? {
//        val sharedPref =
//            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
//        sharedPref.getString(SHARED_PREF_DRINKING_UNIT, null)?.let {
//            return jacksonObjectMapper().readValue<AlcoholUnit>(it)
//        }
//        return null
//    }
//
//    fun getWantedBloodLevelSharedPref(): Float? {
//        val sharedPref =
//            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
//        sharedPref.getFloat(SHARED_PREF_DRINKING_WANTED_BLOOD_LEVEL, -1F).let {
//            if (it != -1F) {
//                return it
//            }
//        }
//        return null
//    }

    fun getDrinkingCalculatorSharedPref(): DrinkingCalculator? {
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        sharedPref.getString(SHARED_PREF_DRINKING_CALCULATOR, null)?.let {
            return jacksonObjectMapper().registerModule(JavaTimeModule())
                .readValue<DrinkingCalculator>(it)
        }
        return null
    }

    private fun clearDrinkingValuesSharedPref() {
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove(SHARED_PREF_DRINKING_TIMES)
            remove(SHARED_PREF_DRINKING_CALCULATOR)
        }.apply()
    }

    private fun saveDrinkingValuesToSharedPref(
        drinkingTimes: List<LocalDateTime>,
        calculator: DrinkingCalculator
    ) {
        val mapper = jacksonObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val calculatorAsJson = jacksonObjectMapper().writeValueAsString(calculator)
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(SHARED_PREF_DRINKING_TIMES, drinkingTimes.toString())
            putString(SHARED_PREF_DRINKING_CALCULATOR, calculatorAsJson)
        }.apply()
    }
}
