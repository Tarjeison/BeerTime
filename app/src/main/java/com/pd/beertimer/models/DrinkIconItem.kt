package com.pd.beertimer.models

import androidx.annotation.DrawableRes

class DrinkIconItem(
    @DrawableRes val drinkId: Int,
    val iconString: String,
    var selected: Boolean = false
)
