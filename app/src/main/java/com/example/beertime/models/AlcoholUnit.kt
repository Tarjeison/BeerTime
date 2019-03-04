package com.example.beertime.models

enum class AlcoholUnit {
    BIG_BEER {
        override fun gramPerUnit(): Double {
            return 500*0.047*0.789
        }
    },
    SMALL_BEER {
        override fun gramPerUnit(): Double {
            return 333*0.047*0.789
        }
    },
    WINE {
        override fun gramPerUnit(): Double {
            return 150*0.125*0.789
        }
    };
    abstract fun gramPerUnit(): Double
}