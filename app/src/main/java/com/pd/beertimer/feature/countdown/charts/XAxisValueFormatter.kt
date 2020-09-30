package com.pd.beertimer.feature.countdown.charts

import com.github.mikephil.charting.components.AxisBase

import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDateTime


class XAxisValueFormatter(private val mValues: Array<LocalDateTime>) :
    ValueFormatter() {
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        // "value" represents the position of the label on the axis (x or y)
        return "${mValues[value.toInt()].hour}:${mValues[value.toInt()].minute}"
    }
}