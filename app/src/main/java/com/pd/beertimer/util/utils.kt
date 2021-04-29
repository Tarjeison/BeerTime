package com.pd.beertimer.util

import android.content.Context
import android.text.format.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

fun LocalDateTime.toHourMinuteString(context: Context, showAmPm: Boolean = false): String {
    val pattern = if (DateFormat.is24HourFormat(context)) {
        "HH:mm"
    } else {
        if (showAmPm) "hh:mm a" else "hh:mm"
    }
    return this.format(DateTimeFormatter.ofPattern(pattern)).replace(" ", "")
}
