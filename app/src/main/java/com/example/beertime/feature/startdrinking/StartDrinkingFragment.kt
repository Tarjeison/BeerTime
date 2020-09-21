package com.example.beertime.feature.startdrinking

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.beertime.MainActivity
import com.example.beertime.NotificationBroadcast
import com.example.beertime.R
import com.example.beertime.feature.countdown.CountDownController
import com.example.beertime.feature.profile.ProfileViewModel
import com.example.beertime.models.AlcoholUnit
import com.example.beertime.util.AlcoholCalculator
import com.example.beertime.util.SHARED_PREF_BEER_TIME
import com.example.beertime.util.SHARED_PREF_DRINKING_TIMES
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_startdrinking.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Duration
import java.time.LocalDateTime

class StartDrinkingFragment : Fragment(), AlcoholAdapterCallback {

    private var wantedBloodLevel = 0f
    private var hoursDrinking: Int = 0
    private var preferredAlcoholUnit: AlcoholUnit? = null

    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var alcoholAdapter: AlcoholAdapter
    private lateinit var startDrinkingViewModel: StartDrinkingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_startdrinking, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startDrinkingViewModel = StartDrinkingViewModel()
        alcoholAdapter = AlcoholAdapter(startDrinkingViewModel.getAlcoholUnits(), this)
        rvAlcoholUnit.layoutManager =
            GridLayoutManager(view.context, 4)
        rvAlcoholUnit.adapter = alcoholAdapter
        alcoholAdapter.notifyDataSetChanged()

        initSeekBars()
        initStartDrinkingButton()
    }

    private fun initSeekBars() {
        sbBloodLevel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                // TODO: Proper strings
                wantedBloodLevel = (p1.toFloat() / 1000)
                tvBloodLevelValue.text = wantedBloodLevel.toString() + "%"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        sbHours.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                // TODO: Proper strings
                hoursDrinking = p1
                tvHoursValue.text = hoursDrinking.toString() + "h"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    private fun validateValues(): Boolean {
        when {
            wantedBloodLevel == 0f -> {
                Snackbar.make(
                    clStartDrinking,
                    R.string.error_startdrinking_wanted_level_0,
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            hoursDrinking == 0 -> {
                Snackbar.make(
                    clStartDrinking,
                    R.string.error_startdrinking_hours_drinking_0,
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            preferredAlcoholUnit == null -> {
                Snackbar.make(
                    clStartDrinking,
                    R.string.error_startdrinking_select_unit,
                    Snackbar.LENGTH_LONG
                ).show()
                return false
            }
            else -> {
                return true
            }
        }

    }

    private fun initStartDrinkingButton() {
        bStartDrinking.setOnClickListener {
            if (validateValues()) {
                if (isDrinking()) {
                    alertAlreadyDrinking()
                } else {
                    startDrinking()
                }
            }
        }
    }

    private fun startDrinking() {
        this.context?.let {
            val profile = profileViewModel.getUserProfile(it)
            if (profile == null) {
                Snackbar.make(
                    clStartDrinking,
                    R.string.error_no_user_profile_found,
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                val drinkingTimes = AlcoholCalculator(
                    profile,
                    wantedBloodLevel,
                    LocalDateTime.now().plusHours(hoursDrinking.toLong()),
                    AlcoholUnit("Test", 500*0.047*0.7, R.drawable.ic_icon_beer)
                ).calculateDrinkingTimes()

                saveDrinkingTimesToSharePreferences(drinkingTimes)
                createNotificationAlarms(drinkingTimes)
                findNavController().navigate(R.id.action_startDrinkingFragment_to_countDownFragment)
            }
        }
    }

    private fun createNotificationAlarms(drinkingTimes: MutableList<LocalDateTime>) {
        val intent = Intent(context, NotificationBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager?
        alarmManager?.let { aManager ->

            val now = LocalDateTime.now()
            val nowMillis = System.currentTimeMillis()
            drinkingTimes.forEach {
                val duration = Duration.between(now, it)
                aManager.set(AlarmManager.RTC_WAKEUP, nowMillis + duration.toMillis(), pendingIntent)
            }
        }
    }

    private fun saveDrinkingTimesToSharePreferences(drinkingTimes: List<LocalDateTime>) {
        val sharedPref =
            requireContext().getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(SHARED_PREF_DRINKING_TIMES, drinkingTimes.toString())
        }.apply()
    }

    private fun alertAlreadyDrinking() {
        AlertDialog.Builder(this.context)
            .setTitle(R.string.startdrinking_are_you_sure)
            .setMessage(R.string.startdrinking_already_drinking)
            .setPositiveButton(R.string.yes
            ) { _, _ -> startDrinking() }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun isDrinking(): Boolean {
        val sharedPref =
            requireContext().getSharedPreferences(SHARED_PREF_BEER_TIME, Context.MODE_PRIVATE)
        return (sharedPref.getString(SHARED_PREF_DRINKING_TIMES, null) !=
                null)
    }

    override fun onItemSelected(name: String) {
        val test = startDrinkingViewModel.getAlcoholUnits().apply {
            this.forEach {
                if (it.name == name) {
                    preferredAlcoholUnit = it
                    it.isSelected = true
                } else {
                    it.isSelected = false
                }
            }
        }.toList()
        alcoholAdapter.setData(test)
    }
}
