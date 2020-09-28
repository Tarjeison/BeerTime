package com.pd.beertimer.feature.countdown

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.pd.beertimer.R
import com.pd.beertimer.util.AlarmUtils
import kotlinx.android.synthetic.main.fragment_timer.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class CountDownFragment : Fragment() {

    private lateinit var countDownTimer: CountDownTimer
    private var drinkingTimes: List<LocalDateTime>? = null

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
            drinkingTimes = AlarmUtils(it).getExistingDrinkTimesFromSharedPref()
        }
        if (drinkingTimes == null) {
            setViewsDrinkingNotStarted()
        } else {
            setViewsDrinkingStarted()
            setupCountDownTimer()
        }
    }
}
