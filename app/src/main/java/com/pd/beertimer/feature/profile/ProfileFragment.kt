package com.pd.beertimer.feature.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.pd.beertimer.R
import com.pd.beertimer.databinding.FragmentProfileBinding
import com.pd.beertimer.models.Gender
import com.pd.beertimer.models.UserProfile
import com.pd.beertimer.util.SHARED_PREF_USES_LITERS
import com.pd.beertimer.util.ToastHelper
import com.pd.beertimer.util.viewBinding
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val profileViewModel: ProfileViewModel by viewModel()
    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val sharedPreferences: SharedPreferences by inject()


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.clProfile.setOnTouchListener { v, _ ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v?.windowToken, 0)
            binding.etWeight.clearFocus()
            true
        }

        val userProfile = profileViewModel.getUserProfile()
        userProfile?.let {
            binding.etWeight.setText(it.weight.toString())
            when (it.gender) {
                Gender.FEMALE -> {
                    ibFemale.isSelected = true
                    ibFemale.setBackgroundColor(Color.LTGRAY)
                }
                Gender.MALE -> {
                    ibMale.isSelected = true
                    ibMale.setBackgroundColor(Color.LTGRAY)
                }
            }
        }
        binding.bSave.setOnClickListener(createSaveClickListener())
        binding.ibMale.setOnClickListener {
            it.setBackgroundColor(Color.LTGRAY)
            it.isSelected = true
            binding.ibFemale.setBackgroundColor(Color.WHITE)
            binding.ibFemale.isSelected = false
        }
        binding.ibFemale.setOnClickListener {
            it.setBackgroundColor(Color.LTGRAY)
            it.isSelected = true
            binding.ibMale.setBackgroundColor(Color.WHITE)
            binding.ibMale.isSelected = false
        }
        setupRadioGroup()
    }

    private fun setupRadioGroup() {
        val isUsingLiters = sharedPreferences.getBoolean(SHARED_PREF_USES_LITERS, true)
        binding.rUnit.check(if (isUsingLiters) R.id.bLiter else R.id.bOunce)
        binding.rUnit.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.bLiter -> saveUnitToSharedPreferences(true)
                R.id.bOunce -> saveUnitToSharedPreferences(false)
            }
        }
    }

    private fun saveUnitToSharedPreferences(isLiters: Boolean) {
        sharedPreferences.edit().putBoolean(SHARED_PREF_USES_LITERS, isLiters).apply()
    }

    private fun createSaveClickListener(): View.OnClickListener {
        return View.OnClickListener {
            if (fieldsAreSet()) {
                val profile = UserProfile(
                    if (binding.ibFemale.isSelected) {
                        Gender.FEMALE
                    } else {
                        Gender.MALE
                    },
                    binding.etWeight.text?.toString()?.toInt() ?: 0
                )
                profileViewModel.saveUserProfile(profile)
                ToastHelper.createToast(
                    layoutInflater,
                    context,
                    R.string.profile_updated,
                    R.drawable.ic_superhero_pineapple
                )

            }

        }
    }

    private fun fieldsAreSet(): Boolean {
        return if (!(binding.ibMale.isSelected || binding.ibFemale.isSelected)) {
            ToastHelper.createToast(
                layoutInflater,
                context,
                R.string.profile_error_no_gender,
                R.drawable.ic_pineapple_confused
            )
            false
        } else if (binding.etWeight.text.isEmpty()) {
            ToastHelper.createToast(
                layoutInflater,
                context,
                R.string.profile_blank_weight,
                R.drawable.ic_pineapple_confused
            )
            false
        } else {
            true
        }
    }
}
