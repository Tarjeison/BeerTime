package com.pd.beertimer.feature.drinks

import androidx.recyclerview.widget.DiffUtil

class DrinkIconAdapterDiffUtil : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return 3
    }

    override fun getNewListSize(): Int {
        return 3
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        TODO("Not yet implemented")
    }
}