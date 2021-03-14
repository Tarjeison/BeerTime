package com.pd.beertimer.feature.drinks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pd.beertimer.R
import com.pd.beertimer.models.DrinkIconItem
import com.pd.beertimer.room.Drink
import com.pd.beertimer.util.Failure
import com.pd.beertimer.util.Result
import com.pd.beertimer.util.Success
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

class AddDrinkViewModel(private val drinkRepository: DrinkRepository) : ViewModel() {

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
        val drinkValidFormat =  drinkVolume?.toFloatOrNull() ?: run {
            _addDrinkResultLiveData.postValue(Failure(
                Pair(AddDrinkInputField.DRINK_VOLUME, R.string.add_drink_missing_volume))
            )
            return null
        }
        return when {
            drinkValidFormat < 0.02F -> {
                _addDrinkResultLiveData.postValue(Failure(
                    Pair(AddDrinkInputField.DRINK_VOLUME, R.string.add_drink_volume_too_low))
                )
                null
            }
            drinkValidFormat > 1F -> {
                _addDrinkResultLiveData.postValue(Failure(
                    Pair(AddDrinkInputField.DRINK_VOLUME, R.string.add_drink_volume_too_high))
                )
                null
            } else -> {
                drinkValidFormat
            }
        }
    }
}

enum class AddDrinkInputField {
    DRINK_NAME, DRINK_VOLUME, DRINK_PERCENTAGE
}