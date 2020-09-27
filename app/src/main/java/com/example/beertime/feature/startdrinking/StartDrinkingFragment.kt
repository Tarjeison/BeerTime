package com.example.beertime.feature.startdrinking

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beertime.R
import com.example.beertime.feature.profile.ProfileViewModel
import com.example.beertime.models.AlcoholUnit
import com.example.beertime.util.AlarmUtils
import com.example.beertime.util.AlcoholCalculator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_startdrinking.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime

class StartDrinkingFragment : Fragment(), AlcoholAdapterV2Callback {

    private var wantedBloodLevel = 0f
    private var hoursDrinking: Int = 0
    private var preferredAlcoholUnit: AlcoholUnit? = null

    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var alcoholAdapter: AlcoholAdapterV2
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
        alcoholAdapter = AlcoholAdapterV2(startDrinkingViewModel.getAlcoholUnits(), this)
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
                wantedBloodLevel = (p1.toFloat() / 1000)
                tvBloodLevelValue.text = String.format(getString(R.string.startdrinking_percentage),
                    wantedBloodLevel.toString())
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
                tvHoursValue.text = String.format(getString(R.string.startdrinking_hour),
                    hoursDrinking.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    private fun validateValuesAndCreateCalculation(): AlcoholCalculator? {

        val selectedBloodLevel = if (wantedBloodLevel != 0f) {
            wantedBloodLevel
        } else {
            createSnackBar(R.string.error_startdrinking_wanted_level_0)
            return null
        }

        val selectedHoursDrinking = if (hoursDrinking != 0) {
            hoursDrinking
        } else {
            createSnackBar(R.string.error_startdrinking_hours_drinking_0)
            return null
        }

        val selectedPreferredAlcoholUnit = preferredAlcoholUnit ?: kotlin.run {
            createSnackBar(R.string.error_startdrinking_select_unit)
            return null
        }

        val profile = profileViewModel.getUserProfile() ?: kotlin.run {
            createSnackBar(R.string.error_no_user_profile_found)
            return null
        }

        return AlcoholCalculator(profile,
            selectedBloodLevel,
            LocalDateTime.now().plusHours(selectedHoursDrinking.toLong()),
            selectedPreferredAlcoholUnit
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

    private fun startDrinking(calculator: AlcoholCalculator) {
        context?.let {
            val drinkingTimes = calculator.calculateDrinkingTimes()
            val alarmUtils = AlarmUtils(it)
            alarmUtils.deleteExistingAlarms()
            alarmUtils.setAlarmsAndStoreTimesToSharedPref(drinkingTimes)
            findNavController().navigate(R.id.action_startDrinkingFragment_to_countDownFragment)
        }
    }

    private fun alertAlreadyDrinking(calculator: AlcoholCalculator) {
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
