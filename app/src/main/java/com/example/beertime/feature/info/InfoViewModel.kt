package com.example.beertime.feature.info

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.beertime.R
import com.example.beertime.models.InfoDto

class InfoViewModel(applicationContext: Context): ViewModel() {

    fun getInfoDtos(): List<InfoDto> {
        return infoDtos
    }

    private val infoDtos = listOf(
        InfoDto(
            title = applicationContext.getString(R.string.info_what_is_bac_title),
            infoText = applicationContext.getString(R.string.info_what_is_bac_text),
            iconId = R.drawable.ic_pineapple_thinking
        ),
        InfoDto(
            title = applicationContext.getString(R.string.info_how_calculator_title),
            infoText = applicationContext.getString(R.string.info_how_calculator_text),
            iconId = R.drawable.ic_pineapple_detective
        ),
        InfoDto(
            title = applicationContext.getString(R.string.info_custom_drinks_title),
            infoText = applicationContext.getString(R.string.info_custom_drinks_text),
            iconId = R.drawable.ic_pineapple_scientific
        ),
        InfoDto(
            title = applicationContext.getString(R.string.info_why_not_more_title),
            infoText = applicationContext.getString(R.string.info_why_not_more_text),
            iconId = R.drawable.ic_pineapple_confused
        )
    )
}
