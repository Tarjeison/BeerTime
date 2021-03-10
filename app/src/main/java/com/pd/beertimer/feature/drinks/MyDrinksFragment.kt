package com.pd.beertimer.feature.drinks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pd.beertimer.R
import com.pd.beertimer.databinding.FragmentMyDrinksBinding
import com.pd.beertimer.util.observe
import com.pd.beertimer.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyDrinksFragment : Fragment(R.layout.fragment_my_drinks) {

    private val binding by viewBinding(FragmentMyDrinksBinding::bind)
    private val viewModel by viewModel<MyDrinksViewModel>()

    private lateinit var drinksAdapter: MyDrinksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMyDrinks.layoutManager = LinearLayoutManager(context)
        binding.rvMyDrinks.adapter = getMyDrinksAdapter()
        binding.rvMyDrinks.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        observe(viewModel.drinksLiveData) {
            getMyDrinksAdapter().setData(it)
        }
    }

    private fun getMyDrinksAdapter(): MyDrinksAdapter {
        if (!this::drinksAdapter.isInitialized) {
            drinksAdapter = MyDrinksAdapter(mutableListOf())
        }
        return drinksAdapter
    }
}
