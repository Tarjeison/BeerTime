package com.pd.beertimer.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.pd.beertimer.NotificationBroadcast
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmUtils(context: Context) : ContextWrapper(context) {

    fun setAlarmsAndStoreTimesToSharedPref(localDateTimes: List<LocalDateTime>) {
        saveTimesToSharedPref(localDateTimes)
        val aManagers =
            List(localDateTimes.size) { baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
        for (i in aManagers.indices) {
            val millisTriggerTime =
                localDateTimes[i].atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val alarmIntent = Intent(baseContext, NotificationBroadcast::class.java).let { intent ->
                PendingIntent.getBroadcast(baseContext, millisTriggerTime.toInt(), intent, 0)
            }

            aManagers[i].setExact(
                AlarmManager.RTC_WAKEUP,
                localDateTimes[i].atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
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
                val alarmIntent = Intent(applicationContext, NotificationBroadcast::class.java).let { intent ->
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
        return true
    }

    fun getExistingDrinkTimesFromSharedPref(): List<LocalDateTime>? {
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        sharedPref.getString(SHARED_PREF_DRINKING_TIMES, null)?.let {
            var drinkingTimesString = it.trim('[', ']')
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

    private fun saveTimesToSharedPref(drinkingTimes: List<LocalDateTime>) {
        val sharedPref =
            baseContext.getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(SHARED_PREF_DRINKING_TIMES, drinkingTimes.toString())
        }.apply()
    }
}
