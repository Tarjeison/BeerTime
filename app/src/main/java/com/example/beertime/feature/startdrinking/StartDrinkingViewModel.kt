package com.example.beertime.feature.startdrinking

import androidx.lifecycle.ViewModel
import com.example.beertime.R
import com.example.beertime.models.AlcoholUnit

class StartDrinkingViewModel : ViewModel() {

    private val alcoholUnits = arrayListOf<AlcoholUnit>(
        AlcoholUnit("Beer 0,33L", 333*0.047*0.789, R.drawable.ic_icon_beer),
        AlcoholUnit("A really looong name", 500*0.047*0.789, R.drawable.ic_icon_beer),
        AlcoholUnit("Beer 0,5L", 500*0.047*0.789, R.drawable.ic_icon_beer),
        AlcoholUnit("Wine", 150*0.125*0.789, R.drawable.ic_wine)
    )

    fun getAlcoholUnits(): ArrayList<AlcoholUnit> {
        return alcoholUnits
    }

}
