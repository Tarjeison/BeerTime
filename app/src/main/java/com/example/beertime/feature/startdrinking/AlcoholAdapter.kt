package com.example.beertime.feature.startdrinking

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beertime.R
import com.example.beertime.models.AlcoholUnit
import kotlinx.android.synthetic.main.item_drink.view.*


interface AlcoholAdapterCallback {
    fun onItemSelected(name: String)
}

class AlcoholAdapter(private val alcoholUnits: ArrayList<AlcoholUnit>, private val alcoholAdapterCallback: AlcoholAdapterCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlcoholViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_drink, parent, false )
        )
    }

    override fun getItemCount(): Int = alcoholUnits.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return (holder as AlcoholViewHolder).bind(alcoholUnits[position], alcoholAdapterCallback)
    }

    fun setData(newAlcoholUnits: List<AlcoholUnit>) {
        alcoholUnits.clear()
        alcoholUnits.addAll(newAlcoholUnits)
        this.notifyDataSetChanged()
    }

    class AlcoholViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(alcoholUnit: AlcoholUnit, alcoholAdapterCallback: AlcoholAdapterCallback) {
            itemView.ivItemDrink.setImageDrawable(itemView.context.getDrawable(alcoholUnit.iconId))
            itemView.tvItemDrink.text = alcoholUnit.name

            if (alcoholUnit.isSelected) {
                itemView.setBackgroundColor(Color.LTGRAY)
            } else {
                itemView.setBackgroundColor(Color.WHITE)
            }

            itemView.setOnClickListener {
                alcoholAdapterCallback.onItemSelected(alcoholUnit.name)
            }
        }
    }
}
