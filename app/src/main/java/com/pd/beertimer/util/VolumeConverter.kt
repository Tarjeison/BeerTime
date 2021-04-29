package com.pd.beertimer.util

import android.content.SharedPreferences

const val LITER_TO_OZ_RATIO = 33.8140226F
private const val LITER_DESCRIPTION = "L"
private const val OUNCE_DESCRIPTION = " oz"

class VolumeConverter(private val sharedPreferences: SharedPreferences) {

    fun floatLiterToVolumeString(liter: Float): String {
        val isUsingLiter = sharedPreferences.getBoolean(SHARED_PREF_USES_LITERS, true)
        val floatVolume = if (isUsingLiter) liter else liter * LITER_TO_OZ_RATIO
        val formattedFloat = floatVolume.toString()

        val formattedVolume = if (formattedFloat.length > 4) {
            formattedFloat.take(5)
        } else {
            formattedFloat
        }
        return formattedVolume + if (isUsingLiter) LITER_DESCRIPTION else OUNCE_DESCRIPTION
    }
}
