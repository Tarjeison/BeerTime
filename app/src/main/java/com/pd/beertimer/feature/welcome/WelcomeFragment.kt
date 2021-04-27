package com.pd.beertimer.feature.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.pd.beertimer.MainActivity
import com.pd.beertimer.R
import com.pd.beertimer.databinding.FragmentWelcomeBinding
import com.pd.beertimer.util.viewBinding

class WelcomeFragment: Fragment(R.layout.fragment_welcome) {

    private val binding by viewBinding(FragmentWelcomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnClickListener {
            // Just to consume events
        }
        binding.bFinish.setOnClickListener {
            (activity as? MainActivity)?.onWelcomeScreenDismissed()
            parentFragmentManager.popBackStackImmediate()
        }
    }
}
