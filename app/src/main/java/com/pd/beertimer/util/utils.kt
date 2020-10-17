package com.pd.beertimer.util

import java.time.LocalDateTime

inline fun <T : Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    if (elements.all { it != null }) {
        closure(elements.filterNotNull())
    }
}

fun ordinal(i: Int): String {
    val suffix = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when (i % 100) {
        11, 12, 13 -> i.toString() + "th"
        else -> "$i${suffix[i % 10]}"
    }
}

fun LocalDateTime.toHourMinuteString(): String {
    val minute = if (this.minute > 9) {
        "${this.minute}"
    } else {
        "0${this.minute}"
    }

    val hour = if (this.hour > 9) {
        "${this.hour}"
    } else {
        "0${this.hour}"
    }
    return "${hour}:${minute}"
}