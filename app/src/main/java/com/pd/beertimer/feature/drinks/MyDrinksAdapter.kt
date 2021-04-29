package com.pd.beertimer.feature.drinks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pd.beertimer.R
import com.pd.beertimer.models.MyDrinkItem
import com.pd.beertimer.util.VolumeConverter
import kotlinx.android.synthetic.main.item_drink_v2.view.icDrink
import kotlinx.android.synthetic.main.item_drink_v2.view.tvDrinkName
import kotlinx.android.synthetic.main.item_drink_v2.view.tvPercentAndVolume
import kotlinx.android.synthetic.main.item_my_drink.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class MyDrinksAdapter(
    private val drinkList: MutableList<MyDrinkItem>,
    private val onDeleteClick: (Int) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), KoinComponent {

    private val volumeConverter: VolumeConverter by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DrinkViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_drink, parent, false)
        )
    }

    override fun getItemCount(): Int = drinkList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return (holder as DrinkViewHolder).bind(drinkList[position])
    }

    fun setData(newDrinkList: List<MyDrinkItem>) {
        drinkList.clear()
        drinkList.addAll(newDrinkList)
        this.notifyDataSetChanged()
    }

    inner class DrinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(drinkItem: MyDrinkItem) {
            setIcon(drinkItem.iconName)
            itemView.tvDrinkName.text = drinkItem.name
            itemView.tvPercentAndVolume.text = String.format(
                itemView.context.getString(R.string.startdrinking_percent_volume_drink),
                (drinkItem.percentage * 100).toString(),
                volumeConverter.floatLiterToVolumeString(drinkItem.volume)
            )
            itemView.ivDelete.setOnClickListener {
                onDeleteClick.invoke(drinkItem.id)
            }
        }

        private fun setIcon(iconName: String) {
            val iconResId = itemView.resources.getIdentifier(
                iconName,
                "drawable",
                itemView.context.packageName
            )

            if (iconResId == 0) {
                itemView.icDrink.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.ic_beer)
                )
            } else {
                itemView.icDrink.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, iconResId)
                )
            }
        }
    }
}
