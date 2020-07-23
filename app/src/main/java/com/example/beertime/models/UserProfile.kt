package com.example.beertime.models

import java.lang.Exception

data class UserProfile(var age: Int,
                       var gender: Gender,
                       var weight: Int) {
    fun validAge(): Boolean {
        return age >= 18
    }

    fun validWeight(): Boolean {
        return weight >= 40
    }
}

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