package com.example.beertime.feature.countdown

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.beertime.R
import com.example.beertime.util.SHARED_PREF_BEER_TIME
import com.example.beertime.util.SHARED_PREF_DRINKING_TIMES
import kotlinx.android.synthetic.main.fragment_timer.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class CountDownFragment : Fragment() {

    private lateinit var imageAdapter: ImageAdapter
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

        imageAdapter =
            ImageAdapter(mutableListOf())
        rvAlcoholCount.layoutManager =
            GridLayoutManager(view.context, 5)
        rvAlcoholCount.adapter = imageAdapter

        getDrinkingTimesSharedPref()
        if (drinkingTimes == null) {
            tvTimeToNext.text = this.getText(R.string.countdown_not_started_drinking)
        } else {
            refreshViews()
            setupCountDownTimer()
        }
    }

    private fun refreshViews() {
        drinkingTimes?.let {
            val now = LocalDateTime.now()
            val pastUnits = it.filter { drinkTime ->
                drinkTime < now
            }.size

            if (pastUnits > 0) {
                imageAdapter.setData(MutableList(pastUnits) { R.drawable.ic_icon_beer })
            } else {
                setNoUnitsConsumed()
            }
        }
    }

    private fun setupCountDownTimer() {
        val duration = getDurationToNextDateTime() ?: return
        countDownTimer = object : CountDownTimer(duration.toMillis(), 1000) {
            override fun onFinish() {
                setupCountDownTimer()
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
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours, minutes, seconds
            )
        } else {
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes, seconds
            )
        }
    }

    private fun getDrinkingTimesSharedPref() {
        val sharedPref =
            requireContext().getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        sharedPref.getString(SHARED_PREF_DRINKING_TIMES, null)?.let {
            var drinkingTimesString = it.trim('[', ']')
            drinkingTimesString = drinkingTimesString.replace(" ", "")
            val drinkingTimesStringArray = drinkingTimesString.split(",")
            drinkingTimes = drinkingTimesStringArray.map { drinkingTimeString ->
                LocalDateTime.parse(drinkingTimeString)
            }
        }
    }

    private fun setNoUnitsConsumed() {
        tvAlcoholCountBottom.visibility = View.GONE
        tvAlcoholCount.text = getString(R.string.countdown_enjoy_first)
    }

    override fun onPause() {
        super.onPause()
        countDownTimer.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (drinkingTimes == null) {
            tvTimeToNext.text = this.getText(R.string.countdown_not_started_drinking)
        } else {
            refreshViews()
            setupCountDownTimer()
        }
    }
}
