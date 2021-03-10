package com.pd.beertimer.feature.startdrinking

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.pd.beertimer.BuildConfig
import com.pd.beertimer.R
import com.pd.beertimer.databinding.FragmentStartdrinkingBinding
import com.pd.beertimer.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartDrinkingFragment : Fragment(R.layout.fragment_startdrinking) {

    private val binding by viewBinding(FragmentStartdrinkingBinding::bind)

    private val startDrinkingViewModel: StartDrinkingViewModel by viewModel()
    private val firebaseAnalytics: FirebaseAnalytics by inject()
    private val alarmUtils: AlarmUtils by inject()


    private lateinit var alcoholAdapter: AlcoholAdapterV2


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startDrinkingViewModel.getDrinks()

        observe(startDrinkingViewModel.drinksLiveData) {
            getAlcoholAdapter().setData(it)
            binding.bStartDrinking.visibility = View.VISIBLE
        }
        observe(startDrinkingViewModel.finishBarLiveData) {
            binding.tvHoursValue.text = it.displayString
        }
        observe(startDrinkingViewModel.peakHourLiveData) {
            binding.tvPeakValue.text = it.displayString
        }
        observe(startDrinkingViewModel.wantedBloodLevelLiveData) {
            binding.tvBloodLevelValue.text = it
        }

        binding.rvAlcoholUnit.layoutManager = LinearLayoutManager(context)
        binding.rvAlcoholUnit.adapter = getAlcoholAdapter().also {
            it.notifyDataSetChanged()
        }

        if (BuildConfig.DEBUG) {
            binding.sbBloodLevel.max = 60
        }

        initSeekBars()
        initStartDrinkingButton()
    }

    private fun getAlcoholAdapter(): AlcoholAdapterV2 {
        if (!this::alcoholAdapter.isInitialized) {
            alcoholAdapter = AlcoholAdapterV2(mutableListOf())
        }
        return alcoholAdapter
    }

    private fun initSeekBars() {
        binding.sbBloodLevel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                startDrinkingViewModel.setWantedBloodLevel(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        binding.sbHours.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                startDrinkingViewModel.setFinishDrinkingInHoursMinutes(p1)
                binding.sbPeak.max = p1
                if (startDrinkingViewModel.peakTimeNotSet()) {
                    startDrinkingViewModel.setPeakTimeInHoursMinutes(p1)
                    binding.sbPeak.progress = p1
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        binding.sbPeak.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                startDrinkingViewModel.setPeakTimeInHoursMinutes(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
    }

    private fun createSnackBar(@StringRes resId: Int) {
        Snackbar.make(
            binding.clStartDrinking,
            resId,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun initStartDrinkingButton() {
        binding.bStartDrinking.setOnClickListener {
            val selectedUnit = getAlcoholAdapter().getSelectedUnit()
            if (selectedUnit == null) {
                createSnackBar(R.string.error_startdrinking_select_unit)
                return@setOnClickListener
            }
            val calculationResult =
                startDrinkingViewModel.validateValuesAndCreateCalculation(selectedUnit)
            when (calculationResult) {
                is Success -> {
                    // TODO: Fix this shit
                    if (isDrinking()) {
                        alertAlreadyDrinking(calculationResult.value)
                    } else {
                        startDrinking(calculationResult.value)
                    }
                }
                is Failure -> {
                    createSnackBar(calculationResult.reason)
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
        return alarmUtils.getDrinkingCalculatorSharedPref() != null
    }
}
