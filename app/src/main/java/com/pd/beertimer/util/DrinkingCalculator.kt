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
            ((wantedBloodLevel + 0.015 * dTime.toHours()) * ((userProfile.weight * 1000 * genderConst)))/100
        val numberOfUnitsToDrink = (neededGrams/preferredUnit.gramPerUnit).roundToInt()

        val dDuration = dTime.dividedBy(numberOfUnitsToDrink.toLong())
        val drinkingTimes = mutableListOf<LocalDateTime>()
        for (i in 0..numberOfUnitsToDrink) {
            drinkingTimes.add(startTime.plus(dDuration.multipliedBy(i.toLong())))
        }

        return drinkingTimes
    }

    fun changeDuration(previousDrinkingTimes: List<LocalDateTime>) {
    }
}
