package com.pd.beertimer.feature.countdown

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pd.beertimer.R
import com.pd.beertimer.models.AlcoholUnit
import com.pd.beertimer.models.UserProfile
import com.pd.beertimer.util.DrinkingCalculator
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class CountDownController {

    private var alcoholCalculator: DrinkingCalculator? = null
    private lateinit var countDownTimer: CountDownTimer
    private var drinkingStarted = false
    private var errorMessage = R.string.error_drinking_not_started

    private val countDownLiveData = MutableLiveData<String>()
    private val unitConsumedLiveData = MutableLiveData<List<AlcoholUnit>>()

    private var drinkingUnit: AlcoholUnit? = null
    private var numberOfUnitsConsumed = mutableListOf<AlcoholUnit>()

    fun isDrinkng(): Boolean {
        return drinkingStarted
    }

    fun setAlcoholCalculator(
        userProfile: UserProfile, wantedBloodLevel: Float,
        peakTime: LocalDateTime, alcoholUnit: AlcoholUnit, nConsumed: Int = 0
    ) {

        drinkingStarted = true
        drinkingUnit = alcoholUnit
        numberOfUnitsConsumed.clear()
        unitConsumedLiveData.postValue(numberOfUnitsConsumed)

        Log.d("COUNTDOWN", "Drinking started")
        alcoholCalculator = DrinkingCalculator(
            userProfile,
            wantedBloodLevel,
            peakTime,
            alcoholUnit
        )

        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }

        val drinkingTimes = alcoholCalculator?.calculateDrinkingTimes()

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
        return if (days != 0L) {
            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours, minutes, seconds
            )
        } else {
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes, seconds
            )
        }
    }

    fun onUnitConsumed() {
        drinkingUnit?.let {
            numberOfUnitsConsumed.add(it)
        }
        unitConsumedLiveData.postValue(numberOfUnitsConsumed)

    }

    fun getNumberOfUnitsConsumedLiveData(): MutableLiveData<List<AlcoholUnit>> {
        return unitConsumedLiveData
    }

    fun getCountDownLiveData(): MutableLiveData<String> {
        return countDownLiveData
    }

}
