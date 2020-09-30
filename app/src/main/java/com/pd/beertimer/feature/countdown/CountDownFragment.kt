package com.pd.beertimer.feature.countdown

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.pd.beertimer.R
import com.pd.beertimer.feature.profile.ProfileViewModel
import com.pd.beertimer.models.AlcoholUnit
import com.pd.beertimer.models.UserProfile
import com.pd.beertimer.util.AlarmUtils
import com.pd.beertimer.util.DrinkingCalculator
import com.pd.beertimer.util.ifLet
import kotlinx.android.synthetic.main.fragment_timer.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


class CountDownFragment : Fragment() {

    private lateinit var countDownTimer: CountDownTimer
    private var drinkingTimes: List<LocalDateTime>? = null
    private var currentlyDrinkingUnit: AlcoholUnit? = null
    private var wantedBloodLevel: Float? = null
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setViewsDrinkingStarted() {
        drinkingTimes?.let {
            currentlyDrinkingUnit?.let { unit ->
                ivCurrentlyDrinking.setImageDrawable(context?.getDrawable(unit.iconId))
            }
            ivCountDownPineapple.setImageDrawable(context?.getDrawable(R.drawable.ic_pineapple_smile))
            ivCountDownPineapple.visibility = View.VISIBLE
            clNumOfUnits.visibility = View.VISIBLE

            val now = LocalDateTime.now()
            val pastUnits = it.filter { drinkTime ->
                drinkTime < now
            }.size

            if (pastUnits > 0) {
                tvAlcoholCount.text = String.format(
                    getString(R.string.countdown_drinks_so_far),
                    pastUnits
                )
            } else {
                setNoUnitsConsumed()
            }
        }
    }

    private fun setupCountDownTimer() {
        val duration = getDurationToNextDateTime() ?: return
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        countDownTimer = object : CountDownTimer(duration.toMillis(), 1000) {
            override fun onFinish() {
                setupCountDownTimer()
                setViewsDrinkingStarted()
            }

            override fun onTick(millisUntilFinsihed: Long) {
                tcClock.text = millisToDisplayString(millisUntilFinsihed)
            }

        }.start()
    }

    private fun getDurationToNextDateTime(): Duration? {
        val now = LocalDateTime.now()
        drinkingTimes?.let {
            it.forEach { drinkingTime ->
                if (drinkingTime.isAfter(now)) {
                    return Duration.between(now, drinkingTime)
                }
            }
        }
        return null
    }

    private fun millisToDisplayString(mUntilFinish: Long): String {
        var millisUntilFinished: Long = mUntilFinish
        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
        return if (hours != 0L) {
            String.format(
                "%02d:%02d:%02d",
                hours, minutes, seconds
            )
        } else {
            String.format(
                "%02d:%02d",
                minutes, seconds
            )
        }
    }

    private fun setupBacChart() {
        val profile = profileViewModel.getUserProfile()
        ifLet(
            profile,
            wantedBloodLevel,
            drinkingTimes,
            currentlyDrinkingUnit
        ) { (profile, wantedLevel, dTimes, alcoholUnit) ->
            val bacEstimates = DrinkingCalculator(
                profile as UserProfile,
                wantedLevel as Float,
                (dTimes as List<LocalDateTime>).last(),
                alcoholUnit as AlcoholUnit
            ).generateBACPrediction(dTimes as List<LocalDateTime>)
            bacEstimates?.let { estimates->
                val bac: List<Entry> = estimates.map {
                    if (estimates.indexOf(it) == estimates.lastIndex) {

                        Entry(
                            estimates.indexOf(it).toFloat(),
                            it.first,
                            requireContext().getDrawable(R.drawable.ic_finish_flag)
                        )
                    } else {
                        Entry(
                            estimates.indexOf(it).toFloat(),
                            it.first,
                            requireContext().getDrawable(alcoholUnit.iconId)
                        )

                    }
                    //Entry(estimates.indexOf(it).toFloat(), it.first, requireContext().getDrawable(alcoholUnit.iconId))
                    //, requireContext().getDrawable(alcoholUnit.iconId)
                }
                val set1 = LineDataSet(bac, "")

                set1.mode = LineDataSet.Mode.CUBIC_BEZIER
                set1.cubicIntensity = 0.15f
                set1.setDrawFilled(true)
                set1.setDrawCircles(false)
                set1.lineWidth = 1f
//                set1.circleHoleColor = Color.BLACK
//                set1.circleRadius = 1.5f
//                set1.setCircleColor(Color.WHITE)
//                set1.highLightColor = Color.rgb(244, 117, 117)
                set1.color = Color.BLACK
                set1.fillColor = Color.WHITE
                set1.fillAlpha = 100
                set1.setDrawHorizontalHighlightIndicator(false)


                // draw selection line as dashed
                set1.enableDashedHighlightLine(10f, 5f, 0f)


                // customize legend entry
                set1.setDrawVerticalHighlightIndicator(false)
                set1.fillFormatter = IFillFormatter { _, _ ->
                    chartBac.axisLeft.axisMinimum
                }

                // LimitLine
                val ll1 = LimitLine(wantedLevel as Float)
                ll1.lineWidth = 1f
                ll1.enableDashedLine(5f, 5f, 0f)
                ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
                ll1.textSize = 10f
                chartBac.axisLeft.addLimitLine(ll1)

                val xAxisTimeStamps = bacEstimates.map {
                    val minute = if (it.second.minute > 9) {
                        "${it.second.minute}"
                    } else {
                        "0${it.second.minute}"
                    }

                    val hour = if (it.second.hour > 9) {
                        "${it.second.hour}"
                    } else {
                        "0${it.second.hour}"
                    }
                    "${hour}:${minute}"
                }
                chartBac.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisTimeStamps)
                chartBac.xAxis.granularity = 1f

                // drawables only supported on api level 18 and above
                val drawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.fade_orange)
                set1.fillDrawable = drawable

                // create a data object with the data sets
                val data = LineData(set1)
                // data.setValueTypeface(tfLight)
                data.setValueTextSize(9f)
                data.setDrawValues(false)


                // no description text
                chartBac.description.isEnabled = false

                // draw points over time
                chartBac.animateX(250)
                chartBac.data = data
                chartBac.invalidate()
            }

        }
    }

    private fun setViewsDrinkingNotStarted() {
        tvTimeToNext.text = getText(R.string.countdown_not_started_drinking)
        tcClock.text = getString(R.string.countdown_drinking_clock_not_started)
        ivCountDownPineapple.setImageDrawable(context?.getDrawable(R.drawable.ic_pineapple_sleeping))
        clNumOfUnits.visibility = View.INVISIBLE
    }

    private fun setNoUnitsConsumed() {
        tvAlcoholCount.text = getString(R.string.countdown_enjoy_first)
    }

    override fun onPause() {
        super.onPause()
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            val alarmUtils = AlarmUtils(it)
            drinkingTimes = alarmUtils.getExistingDrinkTimesFromSharedPref()
            currentlyDrinkingUnit = alarmUtils.getCurrentlyDrinkingAlcoholUnitSharedPref()
            wantedBloodLevel = alarmUtils.getWantedBloodLevelSharedPref()
        }
        if (drinkingTimes == null) {
            setViewsDrinkingNotStarted()
        } else {
            setViewsDrinkingStarted()
            setupCountDownTimer()
            setupBacChart()
        }
    }
}
