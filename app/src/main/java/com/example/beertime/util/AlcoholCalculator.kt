package com.example.beertime.util

import com.example.beertime.models.AlcoholUnit
import com.example.beertime.models.Gender
import com.example.beertime.models.UserProfile
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToInt

class AlcoholCalculator(
    private val userProfile: UserProfile,
    private val wantedBloodLevel: Float,
    private val peakTime: LocalDateTime,
    private val preferredUnit: AlcoholUnit
) {


    fun calculateDrinkingTimes(): MutableList<LocalDateTime> {
        val genderConst = when (userProfile.gender) {
            Gender.MALE -> 0.68
            Gender.FEMALE -> 0.55
        }

        val startTime = LocalDateTime.now()
        val dTime = Duration.between(startTime, peakTime)

        val neededGrams =
            ((wantedBloodLevel + 0.015 * dTime.toHours()) * ((userProfile.weight * 1000 * genderConst)))/100
        val numberOfUnitsToDrink = (neededGrams/preferredUnit.gramPerUnit).roundToInt()

        val dDuration = dTime.dividedBy(numberOfUnitsToDrink.toLong())
        val drinkingTimes = mutableListOf<LocalDateTime>()
        for (i in 1..numberOfUnitsToDrink) {
            drinkingTimes.add(startTime.plus(dDuration.multipliedBy(i.toLong())))
        }

        return drinkingTimes
    }
}
