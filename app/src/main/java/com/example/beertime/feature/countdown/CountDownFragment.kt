package com.example.beertime.feature.countdown

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.beertime.R
import kotlinx.android.synthetic.main.fragment_timer.*
import org.koin.android.ext.android.inject

class CountDownFragment : Fragment() {

    private val countDownController: CountDownController by inject()
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!countDownController.isDrinkng()) {
            tvTimeToNext.text = this.getText(R.string.countdown_not_started_drinking)
        }
        imageAdapter =
            ImageAdapter(mutableListOf())
        rvAlcoholCount.layoutManager =
            GridLayoutManager(view.context, 5)
        rvAlcoholCount.adapter = imageAdapter

        observeCountDown()
        observeNumberOfUnits()
    }

    private fun observeCountDown() {
        countDownController.getCountDownLiveData()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                tcClock.text = it
            })
    }

    private fun observeNumberOfUnits() {
        countDownController.getNumberOfUnitsConsumedLiveData()
            .observe(viewLifecycleOwner, Observer {
                val consumedUnitImageIds = it.map { alcoholUnit ->
                    alcoholUnit.iconId
                }

                if (consumedUnitImageIds.isEmpty()) {
                    setNoUnitsConsumed()
                } else {
                    tvAlcoholCountBottom.visibility = View.VISIBLE
                    tvAlcoholCount.text = getString(R.string.countdown_drinks_so_far)
                    imageAdapter.setData(consumedUnitImageIds)
                }
            })
    }

    private fun setNoUnitsConsumed() {
        tvAlcoholCountBottom.visibility = View.GONE
        tvAlcoholCount.text = getString(R.string.countdown_enjoy_first)
    }
}
