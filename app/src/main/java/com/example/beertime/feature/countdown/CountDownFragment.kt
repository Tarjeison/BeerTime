package com.example.beertime.feature.countdown

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beertime.R
import kotlinx.android.synthetic.main.fragment_timer.*
import org.koin.android.ext.android.inject

class CountDownFragment : Fragment() {

    private val countDownController: CountDownController by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeCountDown()
    }

    private fun observeCountDown() {
        countDownController.getCountDownLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            tcClock.text = it
        } )
    }
}
