package com.example.beertime.util

import com.example.beertime.models.AlcoholUnit
import com.example.beertime.models.DrinkingCalculation
import com.example.beertime.models.Gender
import com.example.beertime.models.UserProfile
import java.time.Duration
import java.time.LocalDateTime

class AlcoholCalculator(
    private val userProfile: UserProfile,
    private val wantedBloodLevel: Float,
    private val peakTime: LocalDateTime,
    private val preferredUnit: AlcoholUnit
) {


    fun calculateDrinkingTimes(): DrinkingCalculation {
        val genderConst = when (userProfile.gender) {
            Gender.MALE -> 0.68
            Gender.FEMALE -> 0.55
        }

        val dTime = Duration.between(LocalDateTime.now(), peakTime)

        val neededGrams =
            (wantedBloodLevel + 0.15 * dTime.toHours()) * ((userProfile.weight * genderConst))
        var numberOfUnitsToDrink = 1
        while (preferredUnit.gramPerUnit() * numberOfUnitsToDrink < neededGrams) {
            numberOfUnitsToDrink++
        }
        return DrinkingCalculation(dTime.dividedBy(numberOfUnitsToDrink.toLong()), numberOfUnitsToDrink)
    }
}
