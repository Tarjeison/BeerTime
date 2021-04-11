package com.pd.beertimer.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(var gender: Gender,
                       var weight: Int)

enum class Gender {
    MALE,
    FEMALE;
    companion object {
        fun stringToGender(s: String): Gender {
            return when (s) {
                "MALE" -> MALE
                "FEMALE" -> FEMALE
                else -> throw (Exception("No gender found in string"))
            }
        }
    }
}