package com.pd.beertimer.feature.drinks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pd.beertimer.models.MyDrinkItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MyDrinksViewModel(private val drinkRepository: DrinkRepository): ViewModel() {

    private val _drinksLiveData = MutableLiveData<List<MyDrinkItem>>()
    val drinksLiveData: LiveData<List<MyDrinkItem>> = _drinksLiveData

    init {
        drinkRepository.getDrinks().onEach { drinkList ->
            _drinksLiveData.postValue(drinkList.map { it.toDrinkItem() })
        }.launchIn(viewModelScope)
    }

    fun deleteDrink(drinkId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            drinkRepository.delete(drinkId)
        }
    }
}
