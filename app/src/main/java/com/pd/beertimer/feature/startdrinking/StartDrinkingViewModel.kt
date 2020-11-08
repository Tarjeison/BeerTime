package com.pd.beertimer.feature.startdrinking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pd.beertimer.feature.drinks.DrinkRepository
import com.pd.beertimer.models.AlcoholUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartDrinkingViewModel(private val drinkRepository: DrinkRepository) : ViewModel() {

    private val _drinksLiveData = MutableLiveData<List<AlcoholUnit>>()
    val drinksLiveData: LiveData<List<AlcoholUnit>> get() = _drinksLiveData

    fun getDrinks(){
        viewModelScope.launch(Dispatchers.IO) {
            drinkRepository.getDrinks().let {
                if (it.isNullOrEmpty()) {
                    _drinksLiveData.postValue(alcoholUnits)
                } else {
                    _drinksLiveData.postValue(it.map { drink ->
                        drink.toAlcoholUnit()
                    })
                }
            }
        }
    }

    private val alcoholUnits = arrayListOf(
        AlcoholUnit("Small beer", 0.33F, 0.047F, "ic_beer"),
        AlcoholUnit("Large beer", 0.50F, 0.047F, "ic_beer"),
        AlcoholUnit("Wine", 0.150F, 0.125F, "ic_wine")
    )
}
