package com.pd.beertimer.util

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.pd.beertimer.models.AlcoholUnit
import com.pd.beertimer.models.Gender
import com.pd.beertimer.models.UserProfile
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToInt

data class DrinkingCalculator(
    val userProfile: UserProfile,
    val wantedBloodLevel: Float,
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val peakTime: LocalDateTime,
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val endTime: LocalDateTime,
    val preferredUnit: AlcoholUnit
) {
    private val genderConst = when (userProfile.gender) {
        Gender.MALE -> 0.68
        Gender.FEMALE -> 0.55
    }

    fun calculateDrinkingTimes(): MutableList<LocalDateTime> {

        val startTime = LocalDateTime.now()
        val drinkingTimes = mutableListOf<LocalDateTime>()
        drinkingTimes.addAll(calculateDrinkInterval(startTime, peakTime, wantedBloodLevel))

        if (Duration.between(peakTime, endTime).toMinutes() > 30F) {
            drinkingTimes.addAll(calculateDrinkInterval(peakTime, endTime, 0F))
        }

        return drinkingTimes
    }

    private fun calculateDrinkInterval(
        startTime: LocalDateTime, endTime: LocalDateTime, wantedBloodLevel: Float
    ): MutableList<LocalDateTime> {
        val drinkingDuration = Duration.between(startTime, endTime)
        val neededGrams =
            ((wantedBloodLevel + 0.015 * drinkingDuration.toMinutes() / 60f) * ((userProfile.weight * 1000 * genderConst))) / 100
        val numberOfUnitsToDrink = (neededGrams / preferredUnit.gramPerUnit).roundToInt()
        if (numberOfUnitsToDrink > 85) return mutableListOf() // TODO: Make this return proper errors
        if (numberOfUnitsToDrink == 0) return mutableListOf() // Nothing to drink
        val dDuration = drinkingDuration.dividedBy(numberOfUnitsToDrink.toLong())
        val drinkingTimes = mutableListOf<LocalDateTime>()
        for (i in 0 until numberOfUnitsToDrink) {
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
                        calculateBac(Duration.between(startTime, drinkingTimes[i]), i),
                        drinkingTimes[i]
                    )
                )
            }
        }
        val lastDateTime = endTime
        bacEstimations.add(
            Pair(
                calculateBac(Duration.between(startTime, lastDateTime), drinkingTimes.size),
                lastDateTime
            )
        )
        return bacEstimations
    }

    private fun calculateBac(duration: Duration, nConsumed: Int): Float {
        return ((preferredUnit.gramPerUnit * nConsumed / (userProfile.weight * 1000 * genderConst)) * 100 - (duration.toMinutes() / 60f) * 0.015).toFloat()
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
