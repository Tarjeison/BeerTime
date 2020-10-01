package com.pd.beertimer.feature.countdown.charts

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_timer.*
import java.time.LocalDateTime

class ChartHelper {
    fun setLineDataSetAttributes(lineDataSet: LineDataSet) {
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.15f
        lineDataSet.setDrawFilled(true)
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 1f
//                set1.circleHoleColor = Color.BLACK
//                set1.circleRadius = 1.5f
//                set1.setCircleColor(Color.WHITE)
//                set1.highLightColor = Color.rgb(244, 117, 117)
        lineDataSet.color = Color.BLACK
        lineDataSet.fillColor = Color.WHITE
        lineDataSet.fillAlpha = 100
        lineDataSet.setDrawHorizontalHighlightIndicator(false)


        // draw selection line as dashed
        lineDataSet.enableDashedHighlightLine(10f, 5f, 0f)


        // customize legend entry
        lineDataSet.setDrawVerticalHighlightIndicator(false)

    }

    fun createLimitLine(drawValue: Float): LimitLine {
        val limitLine = LimitLine(drawValue)
        limitLine.lineWidth = 1f
        limitLine.enableDashedLine(5f, 5f, 0f)
        limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        limitLine.textSize = 10f
        return limitLine
    }

    fun createAxisLabelFormatterFromLocalDateTimeList(list: List<LocalDateTime>): IndexAxisValueFormatter {
        val labels = list.map {
            val minute = if (it.minute > 9) {
                "${it.minute}"
            } else {
                "0${it.minute}"
            }

            val hour = if (it.hour > 9) {
                "${it.hour}"
            } else {
                "0${it.hour}"
            }
            "${hour}:${minute}"
        }

        return IndexAxisValueFormatter(labels)
    }
}
