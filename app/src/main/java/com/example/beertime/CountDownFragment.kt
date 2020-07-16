package com.example.beertime

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.beertime.models.AlcoholUnit
import kotlinx.android.synthetic.main.fragment_timer.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class CountDownFragment : Fragment() {

    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val profileViewModel = ProfileViewModel()
        val calculator = AlcoholCalculator(
            profileViewModel.getUserProfile(view.context)!!,
            1.55f,
            LocalDateTime.now().plusHours(1.toLong()),
            AlcoholUnit.BIG_BEER
        )
        val calc = calculator.calculateDrinkingTimes()
        val d = calc.d
        val n = calc.num
        print(calc)
        countDownTimer = countDown(view, d, n)
    }

    private fun countDown(view: View, d: Duration, num: Int): CountDownTimer {
        return object : CountDownTimer(d.toMillis(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tcClock.text = timeString(millisUntilFinished)
            }

            override fun onFinish() {
                if (num > 0) {
                    countDown(view, d, num - 1)
                    createNotification()
                } else {
                    tvTimeToNext.text = view.context.getText(R.string.timer_finished_text)
                }
            }
        }.start()
    }

    private fun createNotification() {
        val builder = NotificationCompat.Builder(activity!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_icon_beer)
            .setContentTitle("Drink")
            .setContentText("Time to drink")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(activity!!)) {
            // notificationId is a unique int for each notification that you must define
            notify(Random().nextInt(), builder.build())
        }
    }

    // Method to get days hours minutes seconds from milliseconds
    private fun timeString(mUntilFinish: Long): String {
        var millisUntilFinished: Long = mUntilFinish
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
            minutes, seconds
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
