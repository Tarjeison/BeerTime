package com.example.beertime.feature.countdown

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.beertime.R
import com.example.beertime.models.AlcoholUnit
import com.example.beertime.models.DrinkingCalculation
import com.example.beertime.models.UserProfile
import com.example.beertime.util.AlcoholCalculator
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class CountDownController {

    private var alcoholCalculator: AlcoholCalculator? = null
    private lateinit var countDownTimer: CountDownTimer
    private var drinkingStarted = false
    private var errorMessage = R.string.error_drinking_not_started

    private val countDownLiveData = MutableLiveData<String>()
    private val notifierLiveData = MutableLiveData<DrinkingCalculation>()

    var numberOfUnitsConsumed = 0

    fun setAlcoholCalculator(
        userProfile: UserProfile, wantedBloodLevel: Float,
        peakTime: LocalDateTime, alcoholUnit: AlcoholUnit, nConsumed: Int = 0
    ) {

        drinkingStarted = true
        numberOfUnitsConsumed = nConsumed

        Log.d("COUNTDOWN", "Drinking started")
        alcoholCalculator = AlcoholCalculator(
            userProfile,
            wantedBloodLevel,
            peakTime,
            alcoholUnit
        )

        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }

        val calculation = alcoholCalculator?.calculateDrinkingTimes()
        calculation?.let {
            notifierLiveData.postValue(it)
        }
    }

    // Method to get days hours minutes seconds from milliseconds
    fun timeString(mUntilFinish: Long): String {
        var millisUntilFinished: Long = mUntilFinish
        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

        // Format the string
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes, seconds
        )
    }

    fun getCountDownLiveData(): MutableLiveData<String> {
        return countDownLiveData
    }

    fun getNotificationLiveData(): MutableLiveData<DrinkingCalculation> {
        return notifierLiveData
    }
}
