package com.pd.beertimer.feature.me

import androidx.lifecycle.ViewModel
import com.pd.beertimer.R
import com.pd.beertimer.models.MeItem

class MeViewModel : ViewModel() {

    private val meItems = listOf(
        MeItem(R.drawable.ic_superhero_pineapple, R.string.profile, R.id.action_meFragment_to_profileFragment),
        MeItem(R.drawable.ic_cocktail, R.string.me_drinks, R.id.action_meFragment_to_myDrinksFragment)
    )

    fun getMeItems() = meItems
}
