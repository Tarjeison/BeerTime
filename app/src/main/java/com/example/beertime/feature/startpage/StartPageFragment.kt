package com.example.beertime.feature.startpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.beertime.R
import com.example.beertime.feature.profile.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_startpage.*

class StartPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_startpage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val profileViewModel = ProfileViewModel()

        bProfile.setOnClickListener {
            findNavController().navigate(R.id.action_startpageFragment_to_profileFragment)
        }

        if (validProfile(profileViewModel, view.context)) {
            bDrink.setOnClickListener {
                findNavController().navigate(R.id.action_startpageFragment_to_countDownFragment)
            }
        } else {
            bDrink.setOnClickListener {
                Snackbar.make(clStartPage, R.string.missing_profiles, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun validProfile(profileViewModel: ProfileViewModel, context: Context): Boolean {
        val user = profileViewModel.getUserProfile(context)
        user?.let {
            return it.validWeight() && it.validAge()
        }
        return false
    }
}
