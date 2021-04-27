package com.pd.beertimer.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.pd.beertimer.NotificationBroadcast
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneId

private const val REQUEST_CODE: Int = 1337727272

class AlarmUtils(context: Context) : ContextWrapper(context) {

    fun setFirstAlarmAndStoreTimesToSharedPref(
        localDateTimes: List<LocalDateTime>,
        calculator: DrinkingCalculator
    ) {
        saveDrinkingValuesToSharedPref(localDateTimes, calculator)

        // Drop first time as it is when the user started drinking
        localDateTimes.getOrNull(1)?.let {
            val millisTriggerTime = it.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            scheduleAlarmClock(millisTriggerTime)
        }
    }

    fun scheduleAlarmClock(triggerTimeInMs: Long) {
        val am = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(baseContext, NotificationBroadcast::class.java).let { intent ->
            PendingIntent.getBroadcast(
                baseContext, REQUEST_CODE,
                intent,
                0
            )
        }
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTimeInMs, alarmIntent)
        am.setAlarmClock(alarmClockInfo, alarmIntent)
    }

    fun deleteNextAlarm(): Boolean {
        val am = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            Intent(applicationContext, NotificationBroadcast::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    baseContext,
                    REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            }
        am.cancel(alarmIntent)
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

    fun getNextDrinkingTimeFromSharedPref(): Pair<LocalDateTime, Boolean>? {
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        sharedPref.getString(SHARED_PREF_DRINKING_TIMES, null)?.let { drinkingTimesFromSharedPref ->
            var drinkingTimesString = drinkingTimesFromSharedPref.trim('[', ']')
            if (drinkingTimesString.isEmpty()) return null
            drinkingTimesString = drinkingTimesString.replace(" ", "")
            val drinkingTimesStringArray = drinkingTimesString.split(",")
            val drinkingLocalDateTimes = drinkingTimesStringArray.map { drinkingTimeString ->
                LocalDateTime.parse(drinkingTimeString)
            }
            val currentTime = LocalDateTime.now().plusSeconds(10) // Give some leeway
            val nextDrinkingTime =
                drinkingLocalDateTimes.firstOrNull { it > currentTime } ?: return null
            val isLast =
                drinkingLocalDateTimes.indexOf(nextDrinkingTime) == drinkingLocalDateTimes.lastIndex
            return Pair(nextDrinkingTime, isLast)
        }
        return null
    }

    fun getDrinkingCalculatorSharedPref(): DrinkingCalculator? {
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        sharedPref.getString(SHARED_PREF_DRINKING_CALCULATOR, null)?.let {
            return Json.decodeFromString<DrinkingCalculator>(it)
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
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(SHARED_PREF_DRINKING_TIMES, drinkingTimes.toString())
            putString(SHARED_PREF_DRINKING_CALCULATOR, Json.encodeToString(calculator))
        }.apply()
    }
}
