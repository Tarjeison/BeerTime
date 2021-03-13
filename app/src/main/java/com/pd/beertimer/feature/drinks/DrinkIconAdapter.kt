package com.pd.beertimer.feature.drinks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pd.beertimer.R
import com.pd.beertimer.models.DrinkIconItem
import kotlinx.android.synthetic.main.item_drink_icon.view.*

class DrinkIconAdapter(
    private val drinkIconItems: MutableList<DrinkIconItem>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DrinkIconViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_drink_icon, parent, false)
        )
    }

    override fun getItemCount(): Int = drinkIconItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return (holder as DrinkIconViewHolder).bind(drinkIconItems[position])
    }

    inner class DrinkIconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(drinkIconItem: DrinkIconItem) {
            itemView.clDrink.setOnClickListener {
                drinkIconItems.filter { it != drinkIconItem }
                    .forEach { drinkIconItem ->
                        if (drinkIconItem.selected) {
                            drinkIconItem.selected = false
                            notifyItemChanged(drinkIconItems.indexOf(drinkIconItem))
                        }
                    }
                drinkIconItem.selected = true
                itemView.isSelected = true
            }
            itemView.isSelected = drinkIconItem.selected
            itemView.ivDrinkIcon.setImageResource(drinkIconItem.drinkId)
        }
    }
}