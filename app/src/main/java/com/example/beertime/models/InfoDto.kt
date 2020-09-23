package com.example.beertime.models

import androidx.annotation.DrawableRes

data class InfoDto (val title: String, @DrawableRes val iconId: Int, val infoText: String)