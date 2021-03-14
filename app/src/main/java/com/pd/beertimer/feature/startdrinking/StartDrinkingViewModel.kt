package com.pd.beertimer.feature.startdrinking

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.pd.beertimer.R
import com.pd.beertimer.feature.drinks.DrinkRepository
import com.pd.beertimer.models.AlcoholUnit
import com.pd.beertimer.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import kotlin.math.min

class StartDrinkingViewModel(
    private val applicationContext: Context,
    private val drinkRepository: DrinkRepository,
    private val profileStorage: ProfileStorage,
    private val firebaseAnalytics: FirebaseAnalytics
) : ViewModel() {

    private var wantedBloodLevel = 0f
    private var finishDrinkingInHoursMinutes: Pair<Int, Int> = Pair(0, 0)
    private var peakInHoursMinutes: Pair<Int, Int> = Pair(0, 0)
    private var hasSetPeakTime = false

    private val _drinksLiveData = MutableLiveData<List<AlcoholUnit>>()
    val drinksLiveData: LiveData<List<AlcoholUnit>> get() = _drinksLiveData

    private val _peakHourLiveData = MutableLiveData<SeekBarUIModel>()
    val peakHourLiveData: LiveData<SeekBarUIModel> get() = _peakHourLiveData

    private val _finishBarLiveData = MutableLiveData<SeekBarUIModel>()
    val finishBarLiveData: LiveData<SeekBarUIModel> get() = _finishBarLiveData

    private val _wantedBloodLevelLiveData = MutableLiveData<String>()
    val wantedBloodLevelLiveData: LiveData<String> get() = _wantedBloodLevelLiveData

    @ExperimentalCoroutinesApi
    fun getDrinks() {
        drinkRepository.getDrinks().onEach {
            _drinksLiveData.postValue(it.map { drink ->
                drink.toAlcoholUnit()
            })
        }.launchIn(viewModelScope)
    }

    fun setWantedBloodLevel(wantedBloodLevelProgress: Int) {
        this.wantedBloodLevel = (wantedBloodLevelProgress.toFloat() / 100)
        _wantedBloodLevelLiveData.postValue(
            String.format(
                applicationContext.getString(R.string.startdrinking_permille),
                (wantedBloodLevelProgress.toFloat() / 10).toString()
            )
        )
    }

    fun setFinishDrinkingInHoursMinutes(seekBarValue: Int) {
        val hoursDrinking = (seekBarValue / 60) + 1
        var minutesDrinking = (seekBarValue - ((seekBarValue / 60) * 60) + (10 - seekBarValue%10))
        minutesDrinking -= LocalDateTime.now().minute % 10
        finishDrinkingInHoursMinutes = Pair(hoursDrinking, minutesDrinking)
        val seekBarUIModel = SeekBarUIModel(
            displayString = LocalDateTime.now().plusHours(hoursDrinking.toLong())
                .plusMinutes(minutesDrinking.toLong()).toHourMinuteString()
        )
        _finishBarLiveData.postValue(seekBarUIModel)
    }

    fun setPeakTimeInHoursMinutes(seekBarValue: Int) {
        hasSetPeakTime = true
        val hoursDrinking = (seekBarValue / 60) + 1
        var minutesDrinking = (seekBarValue - ((seekBarValue / 60) * 60)) + (10 - seekBarValue%10)
        minutesDrinking -= LocalDateTime.now().minute % 10

        peakInHoursMinutes = Pair(hoursDrinking, minutesDrinking)
        val seekBarUIModel = SeekBarUIModel(
            displayString = LocalDateTime.now().plusHours(hoursDrinking.toLong())
                .plusMinutes(minutesDrinking.toLong()).toHourMinuteString()
        )
        _peakHourLiveData.postValue(seekBarUIModel)
    }

    fun peakTimeNotSet(): Boolean {
        return !hasSetPeakTime
    }


    fun validateValuesAndCreateCalculation(selectedUnit: AlcoholUnit): Result<DrinkingCalculator, Int> {
        val selectedBloodLevel = if (wantedBloodLevel != 0f) {
            wantedBloodLevel
        } else {
            return Failure(R.string.error_startdrinking_wanted_level_0)
        }

        val selectedHoursDrinking = if (finishDrinkingInHoursMinutes.first != 0) {
            finishDrinkingInHoursMinutes
        } else {
            return Failure(R.string.error_startdrinking_hours_drinking_0)
        }

        val selectedPeakHour = if (peakInHoursMinutes.first != 0) {
            peakInHoursMinutes
        } else {
            return Failure(R.string.error_startdrinking_hours_drinking_0)

        }

        val profile = profileStorage.getUserProfile() ?: kotlin.run {
            return Failure(R.string.error_no_user_profile_found)
        }

        firebaseAnalytics.logEvent("pressed_start_drinking") {
            param("bac", selectedBloodLevel.toDouble())
            param("hours", selectedHoursDrinking.first.toDouble())
            param("unit", selectedUnit.toString())
            param("profile", profile.toString())
        }

        return Success(
            DrinkingCalculator(
                userProfile = profile,
                wantedBloodLevel = selectedBloodLevel,
                endTime = LocalDateTime.now().plusHours(selectedHoursDrinking.first.toLong())
                    .plusMinutes(selectedHoursDrinking.second.toLong()),
                peakTime = LocalDateTime.now().plusHours(selectedPeakHour.first.toLong())
                    .plusMinutes(selectedPeakHour.second.toLong()),
                preferredUnit = selectedUnit
            )
        )
    }
}
