package com.pd.beertimer.feature.drinks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.pd.beertimer.R
import com.pd.beertimer.databinding.FragmentAddDrinkBinding
import com.pd.beertimer.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddDrinkFragment : Fragment(R.layout.fragment_add_drink) {

    private val binding by viewBinding(FragmentAddDrinkBinding::bind)
    private val viewModel by viewModel<AddDrinkViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drinkIcons = viewModel.getDrinkIcons()
        binding.rvDrinkIcons.layoutManager = GridLayoutManager(context, drinkIcons.size)
        binding.rvDrinkIcons.adapter = DrinkIconAdapter(drinkIcons.toMutableList())
    }
}
