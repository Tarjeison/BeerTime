package com.example.beertime.feature.startdrinking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beertime.R
import com.example.beertime.models.AlcoholUnit
import kotlinx.android.synthetic.main.item_drink_v2.view.*

interface AlcoholAdapterV2Callback {
    fun onItemSelected(name: String)
}

class AlcoholAdapterV2(private val alcoholUnits: ArrayList<AlcoholUnit>, private val alcoholAdapterCallback: AlcoholAdapterV2Callback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AlcoholViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_drink_v2, parent, false )
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

        fun bind(alcoholUnit: AlcoholUnit, alcoholAdapterCallback: AlcoholAdapterV2Callback) {
            itemView.icDrink.setImageDrawable(itemView.context.getDrawable(alcoholUnit.iconId))
            itemView.tvDrinkName.text = alcoholUnit.name
            itemView.rbDrinkSelect.isChecked = alcoholUnit.isSelected
            itemView.tvPercentAndVolume.text = String.format(
                itemView.context.getString(R.string.startdrinking_percent_volume_drink),
                (alcoholUnit.percentage*100).toString(),
                alcoholUnit.volume.toString()
            )
            itemView.setOnClickListener {
                alcoholAdapterCallback.onItemSelected(alcoholUnit.name)
            }
        }
    }
}
