package com.pd.beertimer.feature.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pd.beertimer.R
import com.pd.beertimer.models.Gender
import com.pd.beertimer.models.UserProfile
import com.pd.beertimer.util.ToastHelper
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userProfile = profileViewModel.getUserProfile()
        userProfile?.let {
            etAge.setText(it.age.toString())
            etWeight.setText(it.weight.toString())
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
        bSave.setOnClickListener(createSaveClickListener())
        ibMale.setOnClickListener {
            it.setBackgroundColor(Color.LTGRAY)
            it.isSelected = true
            ibFemale.setBackgroundColor(Color.WHITE)
            ibFemale.isSelected = false
        }
        ibFemale.setOnClickListener {
            it.setBackgroundColor(Color.LTGRAY)
            it.isSelected = true
            ibMale.setBackgroundColor(Color.WHITE)
            ibMale.isSelected = false
        }
    }

    private fun createSaveClickListener(): View.OnClickListener {
        return View.OnClickListener {
            if (fieldsAreSet()) {
                val profile = UserProfile(
                    etAge.text?.toString()?.toInt() ?: 0,
                    if (ibFemale.isSelected) {
                        Gender.FEMALE
                    } else {
                        Gender.MALE
                    },
                    etWeight.text?.toString()?.toInt() ?: 0
                )
                if (isValidUserProfile(profile)) {
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
    }

    private fun fieldsAreSet(): Boolean {
        return if (!(ibMale.isSelected || ibFemale.isSelected)) {
            ToastHelper.createToast(
                layoutInflater,
                context,
                R.string.profile_error_no_gender,
                R.drawable.ic_pineapple_confused
            )
            false
        } else if (etAge.text.isEmpty()) {
            ToastHelper.createToast(
                layoutInflater,
                context,
                R.string.profile_blank_age,
                R.drawable.ic_pineapple_confused
            )
            false
        } else if (etWeight.text.isEmpty()) {
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

    private fun isValidUserProfile(userProfile: UserProfile): Boolean {
        return if (!userProfile.validAge()) {
            ToastHelper.createToast(
                layoutInflater,
                context,
                R.string.profile_error_age,
                R.drawable.ic_pineapple_confused
            )
            false
        } else if (!userProfile.validWeight()) {
            ToastHelper.createToast(
                layoutInflater,
                context,
                R.string.profile_error_weight,
                R.drawable.ic_pineapple_confused
            )
            false
        } else {
            true
        }
    }
}
