package com.pd.beertimer.feature.drinks

import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pd.beertimer.R
import com.pd.beertimer.models.DrinkIconItem
import com.pd.beertimer.room.Drink
import com.pd.beertimer.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

class AddDrinkViewModel(private val drinkRepository: DrinkRepository, private val sharedPreferences: SharedPreferences) : ViewModel() {

    private val _addDrinkResultLiveData = MutableLiveData<Result<Int, Pair<AddDrinkInputField, Int>>>()
    val addDrinkResultLiveData: LiveData<Result<Int, Pair<AddDrinkInputField, Int>>> = _addDrinkResultLiveData

    private val drinkIconItems = listOf(
        DrinkIconItem(
            drinkId = R.drawable.ic_beer,
            iconString = "ic_beer",
        ),
        DrinkIconItem(
            drinkId = R.drawable.ic_wine,
            iconString = "ic_wine",
        ),
        DrinkIconItem(
            drinkId = R.drawable.ic_cocktail,
            iconString = "ic_cocktail",
        )
    )

    fun getDrinkIcons(): List<DrinkIconItem> {
        return drinkIconItems
    }

    fun addDrink(
        drinkName: String?,
        drinkPercentage: String?,
        drinkVolume: String?,
        drinkIconName: String?
    ) {
        val drinkNameValid = drinkName.takeIf { it?.isNotEmpty() == true } ?: run {
            _addDrinkResultLiveData.postValue(Failure(
                Pair(AddDrinkInputField.DRINK_NAME, R.string.add_drink_missing_name))
            )
            return
        }

        val drinkPercentageValid = validateDrinkPercentage(drinkPercentage) ?: return
        val drinkVolumeValid = validateDrinkVolume(drinkVolume) ?: return
        val drinkIconNameValid = drinkIconName ?: "ic_beer"
        drinkRepository.insert(
            Drink(
                name = drinkNameValid,
                volume = drinkVolumeValid,
                percentage = drinkPercentageValid,
                iconName = drinkIconNameValid
            )
        ).take(1).onEach {
            _addDrinkResultLiveData.postValue(Success(R.string.add_drink_success))
        }.launchIn(viewModelScope)

    }

    private fun validateDrinkPercentage(drinkPercentage: String?): Float? {
        val drinkPercentageNonNullFloat = drinkPercentage?.toFloatOrNull() ?: run {
            _addDrinkResultLiveData.postValue(Failure(
                Pair(AddDrinkInputField.DRINK_PERCENTAGE, R.string.add_drink_missing_percentage))
            )
            return null
        }
        return when {
            drinkPercentageNonNullFloat > 100F -> {
                _addDrinkResultLiveData.postValue(Failure(
                    Pair(AddDrinkInputField.DRINK_PERCENTAGE, R.string.add_drink_to_strong))
                )
                null
            }
            drinkPercentageNonNullFloat < 2F -> {
                _addDrinkResultLiveData.postValue(Failure(
                    Pair(AddDrinkInputField.DRINK_PERCENTAGE, R.string.add_drink_to_weak))
                )
                null
            }
            else -> {
                drinkPercentageNonNullFloat / 100F
            }
        }
    }

    private fun validateDrinkVolume(drinkVolume: String?): Float? {
        val isUsingLiters = sharedPreferences.getBoolean(SHARED_PREF_USES_LITERS, true)
        val drinkValidFormat =  drinkVolume?.toFloatOrNull() ?: run {
            _addDrinkResultLiveData.postValue(Failure(
                Pair(AddDrinkInputField.DRINK_VOLUME, R.string.add_drink_missing_volume))
            )
            return null
        }
        val drinkInLiters = if (isUsingLiters) drinkValidFormat else drinkValidFormat / LITER_TO_OZ_RATIO
        return when {
            drinkInLiters < 0.02F -> {
                @StringRes val errorRes = if (isUsingLiters) {
                    R.string.add_drink_volume_too_low_liter
                } else {
                    R.string.add_drink_volume_too_low_ounce
                }
                _addDrinkResultLiveData.postValue(Failure(
                    Pair(AddDrinkInputField.DRINK_VOLUME, errorRes))
                )
                null
            }
            drinkInLiters > 1F -> {
                @StringRes val errorRes = if (isUsingLiters) {
                    R.string.add_drink_volume_too_high_liter
                } else {
                    R.string.add_drink_volume_too_high_ounce
                }
                _addDrinkResultLiveData.postValue(Failure(
                    Pair(AddDrinkInputField.DRINK_VOLUME, errorRes))
                )
                null
            } else -> {
                drinkInLiters
            }
        }
    }
}

enum class AddDrinkInputField {
    DRINK_NAME, DRINK_VOLUME, DRINK_PERCENTAGE
}