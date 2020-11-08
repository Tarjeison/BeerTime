package com.pd.beertimer.feature.startdrinking

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.pd.beertimer.R
import com.pd.beertimer.feature.profile.ProfileViewModel
import com.pd.beertimer.models.AlcoholUnit
import com.pd.beertimer.util.AlarmUtils
import com.pd.beertimer.util.DrinkingCalculator
import com.pd.beertimer.util.toHourMinuteString
import kotlinx.android.synthetic.main.fragment_startdrinking.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime

class StartDrinkingFragment : Fragment() {

    private var wantedBloodLevel = 0f
    private var finishDrinkingInHoursMinutes: Pair<Int, Int> = Pair(0, 0)
    private var preferredAlcoholUnit: AlcoholUnit? = null
    private var peakInHoursMinutes: Pair<Int, Int> = Pair(0, 0)

    private val startDrinkingViewModel: StartDrinkingViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val firebaseAnalytics: FirebaseAnalytics by inject()

    private var hasSetPeakTime = false

    private lateinit var alcoholAdapter: AlcoholAdapterV2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_startdrinking, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alcoholAdapter = AlcoholAdapterV2(mutableListOf())
        startDrinkingViewModel.getDrinks()
        startDrinkingViewModel.drinksLiveData.observe(viewLifecycleOwner, Observer {
            alcoholAdapter.setData(it)
        })
        rvAlcoholUnit.layoutManager = LinearLayoutManager(context)
        rvAlcoholUnit.adapter = alcoholAdapter
        alcoholAdapter.notifyDataSetChanged()

        initSeekBars()
        initStartDrinkingButton()
    }

    private fun initSeekBars() {
        sbBloodLevel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                // TODO: Proper strings
                wantedBloodLevel = (p1.toFloat() / 100)
                tvBloodLevelValue.text = String.format(
                    getString(R.string.startdrinking_percentage),
                    wantedBloodLevel.toString()
                )
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        sbHours.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                // TODO: Proper strings
                val hoursDrinking = (p1 / 60)
                val minutesDrinking = (p1 - (hoursDrinking * 60))
                sbPeak.max = p1
                finishDrinkingInHoursMinutes = Pair(hoursDrinking, minutesDrinking)
                if (!hasSetPeakTime) {
                    peakInHoursMinutes = Pair(hoursDrinking, minutesDrinking)
                    sbPeak.progress = p1
                    tvPeakValue.text =
                        LocalDateTime.now().plusHours(hoursDrinking.toLong())
                            .plusMinutes(minutesDrinking.toLong()).toHourMinuteString()
                }
                tvHoursValue.text =
                    LocalDateTime.now().plusHours(hoursDrinking.toLong())
                        .plusMinutes(minutesDrinking.toLong()).toHourMinuteString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        sbPeak.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                // TODO: Proper strings
                val hoursDrinking = (p1 / 60)
                val minutesDrinking = (p1 - (hoursDrinking * 60))
                peakInHoursMinutes = Pair(hoursDrinking, minutesDrinking)
                tvPeakValue.text =
                    LocalDateTime.now().plusHours(hoursDrinking.toLong())
                        .plusMinutes(minutesDrinking.toLong()).toHourMinuteString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                hasSetPeakTime = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    private fun validateValuesAndCreateCalculation(): DrinkingCalculator? {

        val selectedBloodLevel = if (wantedBloodLevel != 0f) {
            wantedBloodLevel
        } else {
            createSnackBar(R.string.error_startdrinking_wanted_level_0)
            return null
        }

        val selectedHoursDrinking = if (finishDrinkingInHoursMinutes.first != 0) {
            finishDrinkingInHoursMinutes
        } else {
            createSnackBar(R.string.error_startdrinking_hours_drinking_0)
            return null
        }

        val selectedPeakHour = if (peakInHoursMinutes.first != 0) {
            peakInHoursMinutes
        } else {
            createSnackBar(R.string.error_startdrinking_hours_drinking_0)
            return null
        }

        val selectedPreferredAlcoholUnit = alcoholAdapter.getSelectedUnit() ?: kotlin.run {
            createSnackBar(R.string.error_startdrinking_select_unit)
            return null
        }

        val profile = profileViewModel.getUserProfile() ?: kotlin.run {
            createSnackBar(R.string.error_no_user_profile_found)
            return null
        }

        firebaseAnalytics.logEvent("pressed_start_drinking") {
            param("bac", selectedBloodLevel.toDouble())
            param("hours", selectedHoursDrinking.first.toDouble())
            param("unit", selectedPreferredAlcoholUnit.toString())
            param("profile", profile.toString())
        }

        return DrinkingCalculator(
            userProfile = profile,
            wantedBloodLevel = selectedBloodLevel,
            endTime = LocalDateTime.now().plusHours(selectedHoursDrinking.first.toLong())
                .plusMinutes(selectedHoursDrinking.second.toLong()),
            peakTime = LocalDateTime.now().plusHours(selectedPeakHour.first.toLong())
                .plusMinutes(selectedPeakHour.second.toLong()),
            preferredUnit = selectedPreferredAlcoholUnit
        )

    }

    private fun createSnackBar(@StringRes resId: Int) {
        Snackbar.make(
            clStartDrinking,
            resId,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun initStartDrinkingButton() {
        bStartDrinking.setOnClickListener {
            val calculation = validateValuesAndCreateCalculation()
            calculation?.let {
                if (isDrinking()) {
                    alertAlreadyDrinking(it)
                } else {
                    startDrinking(it)
                }
            }
        }
    }

    private fun startDrinking(calculator: DrinkingCalculator) {
        context?.let {
            val drinkingTimes = calculator.calculateDrinkingTimes()
            val alarmUtils = AlarmUtils(it)
            alarmUtils.deleteExistingAlarms()
            alarmUtils.setAlarmsAndStoreTimesToSharedPref(
                drinkingTimes,
                calculator
            )
            firebaseAnalytics.logEvent("started_drinking") {
                param("drinking_start_time", drinkingTimes.first().toString())
                param("drinking_end_time", drinkingTimes.last().toString())
            }
            findNavController().navigate(R.id.action_startDrinkingFragment_to_countDownFragment)
        }
    }

    private fun alertAlreadyDrinking(calculator: DrinkingCalculator) {
        AlertDialog.Builder(this.context)
            .setTitle(R.string.startdrinking_are_you_sure)
            .setMessage(R.string.startdrinking_already_drinking)
            .setPositiveButton(
                R.string.yes
            ) { _, _ -> startDrinking(calculator) }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun isDrinking(): Boolean {
        return context?.let {
            AlarmUtils(it).getExistingDrinkTimesFromSharedPref() != null
        } ?: false
    }
}
