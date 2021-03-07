package com.pd.beertimer.models

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class MeItem(
    @DrawableRes val imageIdRes: Int,
    @StringRes val name: Int,
    @IdRes val navigationId: Int
)
