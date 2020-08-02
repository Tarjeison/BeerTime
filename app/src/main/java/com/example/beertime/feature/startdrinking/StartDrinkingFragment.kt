package com.example.beertime.feature.startdrinking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.beertime.R
import com.example.beertime.feature.countdown.CountDownController
import com.example.beertime.feature.profile.ProfileViewModel
import com.example.beertime.models.AlcoholUnit
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_startdrinking.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime

class StartDrinkingFragment : Fragment() {

    private var wantedBloodLevel = 0f
    private var hoursDrinking: Int = 0

    private val countDownController: CountDownController by inject()
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_startdrinking, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            else -> {
                return true
            }
        }

    }

    private fun initStartDrinkingButton() {
        bStartDrinking.setOnClickListener {
            if (validateValues()) {
                this.context?.let {
                    val profile = profileViewModel.getUserProfile(it)
                    if (profile == null) {
                        Snackbar.make(
                            clStartDrinking,
                            R.string.error_no_user_profile_found,
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        countDownController.setAlcoholCalculator(
                            profile,
                            wantedBloodLevel,
                            LocalDateTime.now().plusHours(hoursDrinking.toLong()),
                            AlcoholUnit.SMALL_BEER
                        )
                    }
                }
            }
        }
    }
}