package com.pd.beertimer.util

import com.pd.beertimer.models.AlcoholUnit
import com.pd.beertimer.models.Gender
import com.pd.beertimer.models.UserProfile
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToInt

class DrinkingCalculator(
    val userProfile: UserProfile,
    val wantedBloodLevel: Float,
    val peakTime: LocalDateTime,
    val preferredUnit: AlcoholUnit
) {
    private val genderConst = when (userProfile.gender) {
        Gender.MALE -> 0.68
        Gender.FEMALE -> 0.55
    }

    fun calculateDrinkingTimes(): MutableList<LocalDateTime> {

        val startTime = LocalDateTime.now()
        val dTime = Duration.between(startTime, peakTime)

        val neededGrams =
            ((wantedBloodLevel + 0.015 * dTime.toHours()) * ((userProfile.weight * 1000 * genderConst))) / 100
        val numberOfUnitsToDrink = (neededGrams / preferredUnit.gramPerUnit).toInt()

        val dDuration = dTime.dividedBy(numberOfUnitsToDrink.toLong())
        val drinkingTimes = mutableListOf<LocalDateTime>()
        for (i in 0..numberOfUnitsToDrink) {
            drinkingTimes.add(startTime.plus(dDuration.multipliedBy(i.toLong())))
        }

        return drinkingTimes
    }

    fun generateBACPrediction(drinkingTimes: List<LocalDateTime>): List<Pair<Float, LocalDateTime>>? {
        if (drinkingTimes.size < 2) {
            // Not plotworthy
            return null
        }
        val bacEstimations = mutableListOf<Pair<Float, LocalDateTime>>()
        val startTime = drinkingTimes.first()
        for (i in drinkingTimes.indices) {
            if (i == 0) {
                bacEstimations.add(Pair(0F, drinkingTimes[i]))
            } else {
                bacEstimations.add(
                    Pair(
                        calculateBac(Duration.between(startTime, drinkingTimes[i]), i), drinkingTimes[i]
                    )
                )
            }
        }
        // Need to add the last time, which will be one interval from the last notification
        val intervalDuration = Duration.between(drinkingTimes[0], drinkingTimes[1])
        val lastDateTime = drinkingTimes.last().plus(intervalDuration)
        bacEstimations.add(
            Pair(
                calculateBac(Duration.between(startTime, lastDateTime), drinkingTimes.size), lastDateTime
            )
        )
        return bacEstimations
    }

    private fun calculateBac(duration: Duration, nConsumed: Int): Float {
        return ((preferredUnit.gramPerUnit*nConsumed / (userProfile.weight * 1000 * genderConst)) * 100 - duration.toHours() * 0.015).toFloat()
    }

    fun changeDuration(previousDrinkingTimes: List<LocalDateTime>, numConsumed: Int) {
        val gramsConsumed = numConsumed * preferredUnit.gramPerUnit
        val now = LocalDateTime.now()
        val newDuration = Duration.between(now, peakTime)
        val currentBac = (gramsConsumed / (userProfile.weight * 1000 * genderConst)) * 100 -
                Duration.between(previousDrinkingTimes.first(), now).toHours() * 0.015

        val bacAtEndNoMoreUnits = currentBac - (newDuration.toHours() * 0.015)
        if (bacAtEndNoMoreUnits > wantedBloodLevel) {
            // No more drinks needed
        } else {
            val neededGrams =
                ((wantedBloodLevel + 0.015 * newDuration.toHours()) * ((userProfile.weight * 1000 * genderConst))) / 100
        }
    }
}
