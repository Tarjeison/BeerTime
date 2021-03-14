package com.pd.beertimer.feature.drinks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.pd.beertimer.R
import com.pd.beertimer.databinding.FragmentAddDrinkBinding
import com.pd.beertimer.util.Failure
import com.pd.beertimer.util.Success
import com.pd.beertimer.util.observe
import com.pd.beertimer.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddDrinkFragment : Fragment(R.layout.fragment_add_drink) {

    private val binding by viewBinding(FragmentAddDrinkBinding::bind)
    private val viewModel by viewModel<AddDrinkViewModel>()
    private lateinit var iconAdapter: DrinkIconAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drinkIcons = viewModel.getDrinkIcons()
        binding.rvDrinkIcons.layoutManager = GridLayoutManager(context, drinkIcons.size)
        iconAdapter = DrinkIconAdapter(drinkIcons.toMutableList())
        binding.rvDrinkIcons.adapter = iconAdapter

        binding.bAddDrink.setOnClickListener {
            viewModel.addDrink(
                drinkName = binding.tiDrinkName.editText?.text.toString(),
                drinkVolume = binding.tiVolume.editText?.text.toString(),
                drinkPercentage = binding.tiPercentage.editText?.text.toString(),
                drinkIconName = iconAdapter.getSelectedDrinkName()
            )
        }

        observe(viewModel.addDrinkResultLiveData) {
            when (it) {
                is Success -> {
                    findNavController().popBackStack()
                }
                is Failure -> {
                    handleErrorResult(it)
                }
            }
        }
    }

    private fun handleErrorResult(failure: Failure<Pair<AddDrinkInputField, Int>>) {
        when (failure.reason.first) {
            AddDrinkInputField.DRINK_NAME -> {
                binding.tiDrinkName.error = getString(failure.reason.second)
                binding.tiPercentage.error = null
                binding.tiVolume.error = null
            }
            AddDrinkInputField.DRINK_VOLUME -> {
                binding.tiVolume.error = getString(failure.reason.second)
                binding.tiPercentage.error = null
                binding.tiDrinkName.error = null
            }
            AddDrinkInputField.DRINK_PERCENTAGE -> {
                binding.tiPercentage.error = getString(failure.reason.second)
                binding.tiVolume.error = null
                binding.tiDrinkName.error = null
            }
        }
    }
}