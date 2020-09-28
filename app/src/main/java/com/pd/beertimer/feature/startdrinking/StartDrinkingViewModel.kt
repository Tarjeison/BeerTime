package com.pd.beertimer.feature.startdrinking

import androidx.lifecycle.ViewModel
import com.pd.beertimer.R
import com.pd.beertimer.models.AlcoholUnit

class StartDrinkingViewModel : ViewModel() {

    private val alcoholUnits = arrayListOf<AlcoholUnit>(
        AlcoholUnit("Beer 0,33L", 0.333F,0.047F, R.drawable.ic_icon_beer),
        AlcoholUnit("A really looong name", 0.500F,0.047F, R.drawable.ic_icon_beer),
        AlcoholUnit("Beer 0,5L", 0.500F,0.047F, R.drawable.ic_icon_beer),
        AlcoholUnit("Wine", 0.150F,0.125F, R.drawable.ic_wine)
    )

    fun getAlcoholUnits(): ArrayList<AlcoholUnit> {
        return alcoholUnits
    }

}
