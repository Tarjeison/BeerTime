package com.example.beertime.models

import androidx.annotation.DrawableRes

data class AlcoholUnit(
    val name: String,
    val gramPerUnit: Double,
    @DrawableRes val iconId: Int,
    var isSelected: Boolean = false
)

//enum class AlcoholUnit {
//    BIG_BEER {
//        override fun gramPerUnit(): Double {
//            return 500 * 0.047 * 0.789
//        }
//    },
//    SMALL_BEER {
//        override fun gramPerUnit(): Double {
//            return 333*0.047*0.789
//        }
//    },
//    WINE {
//        override fun gramPerUnit(): Double {
//            return 150*0.125*0.789
//        }
//    };
//    abstract fun gramPerUnit(): Double
//
//}
