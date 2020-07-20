package com.example.beertime.feature.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beertime.R
import com.example.beertime.models.Gender
import com.example.beertime.models.UserProfile
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private val profileViewModel = ProfileViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userProfile = profileViewModel.getUserProfile(view.context)
        userProfile?.let {
            etAge.setText(it.age.toString())
            etWeight.setText(it.weight.toString())
            when (it.gender) {
                Gender.FEMALE -> rbFemale.isChecked
                Gender.MALE -> rbMale.isChecked
            }
        }
        bSave.setOnClickListener {

            val profile = UserProfile(
                etAge.text.toString().toInt(),
                if (rgGender.checkedRadioButtonId == rbMale.id) {
                    Gender.MALE
                } else {
                    Gender.FEMALE
                },
                etWeight.text.toString().toFloat()
            )

            print(userProfile)
            if (isValidUserProfile(profile)) {
                profileViewModel.saveUserProfile(view.context, profile)
                Snackbar.make(clProfile, R.string.profile_updated, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidUserProfile(userProfile: UserProfile): Boolean {
        return if (!userProfile.validAge()) {
            Snackbar.make(clProfile, R.string.profile_error_age, Snackbar.LENGTH_LONG).show()
            false
        } else if (!userProfile.validWeight()) {
            Snackbar.make(clProfile, R.string.profile_error_weight, Snackbar.LENGTH_LONG).show()
            false
        } else if (!(rbMale.isChecked || rbFemale.isChecked)) {
            Snackbar.make(clProfile, R.string.profile_error_no_gender, Snackbar.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }
}
