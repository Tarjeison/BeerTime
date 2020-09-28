package com.pd.beertimer.models

import androidx.annotation.DrawableRes

data class InfoDto (val title: String, @DrawableRes val iconId: Int, val infoText: String)