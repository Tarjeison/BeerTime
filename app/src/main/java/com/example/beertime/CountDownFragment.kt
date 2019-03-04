package com.example.beertime

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.beertime.models.AlcoholUnit
import kotlinx.android.synthetic.main.fragment_timer.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class CountDownFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val profileViewModel = ProfileViewModel()
        val calculator = AlcoholCalculator(profileViewModel.getUserProfile(view.context)!!,
            0.1f,
            LocalDateTime.now().plusHours(3.toLong()),
                AlcoholUnit.BIG_BEER)
        val calc = calculator.calculateDrinkingTimes()
        val d = calc.d
        val n = calc.num
        print(calc)
        countDown(view, d, n)
    }

    private fun countDown(view: View, d: Duration, num: Int): CountDownTimer{
        return object: CountDownTimer(d.toMillis(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tcClock.text = timeString(millisUntilFinished)
            }

            override fun onFinish() {
                if (num > 0) {
                    countDown(view, d, num-1)
                } else {
                    tvTimeToNext.text = "FINISH"
                }
            }
        }.start()
    }

    // Method to get days hours minutes seconds from milliseconds
    private fun timeString(mUntilFinish:Long):String{
        var millisUntilFinished:Long = mUntilFinish
        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

        // Format the string
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,seconds
        )
    }
}