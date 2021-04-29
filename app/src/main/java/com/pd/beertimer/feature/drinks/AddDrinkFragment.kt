package com.pd.beertimer.feature.drinks

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.pd.beertimer.R
import com.pd.beertimer.databinding.FragmentAddDrinkBinding
import com.pd.beertimer.util.*
import kotlinx.android.synthetic.main.fragment_add_drink.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddDrinkFragment : Fragment(R.layout.fragment_add_drink) {

    private val binding by viewBinding(FragmentAddDrinkBinding::bind)
    private val viewModel by viewModel<AddDrinkViewModel>()
    private val sharedPreferences: SharedPreferences by inject()
    private lateinit var iconAdapter: DrinkIconAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isUsingLiters = sharedPreferences.getBoolean(SHARED_PREF_USES_LITERS, true)
        tiVolume.setHint(if (isUsingLiters) R.string.add_volume_desc_liter else R.string.add_volume_desc_ounce)
        binding.clAddDrink.setOnTouchListener { v, _ ->
            val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v?.windowToken, 0)
            binding.tiDrinkName.clearFocus()
            binding.tiVolume.clearFocus()
            binding.tiPercentage.clearFocus()
            true
        }

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
