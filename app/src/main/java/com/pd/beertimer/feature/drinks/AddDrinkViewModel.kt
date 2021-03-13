package com.pd.beertimer.feature.drinks

import androidx.lifecycle.ViewModel
import com.pd.beertimer.R
import com.pd.beertimer.models.DrinkIconItem

class AddDrinkViewModel : ViewModel() {

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

}