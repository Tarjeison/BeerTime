package com.pd.beertimer.feature.me

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pd.beertimer.R
import com.pd.beertimer.models.MeItem
import kotlinx.android.synthetic.main.me_item.view.*

class MeAdapter(private val meItems: MutableList<MeItem>, val onClick: (navigationId: Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.me_item, parent, false)
        )
    }

    override fun getItemCount(): Int = meItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return (holder as MeViewHolder).bind(meItems[position])
    }

    inner class MeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(meItem: MeItem) {
            itemView.itemMe.setOnClickListener { onClick.invoke(meItem.navigationId) }
            itemView.ivMe.setImageResource(meItem.imageIdRes)
            itemView.tvMe.text = itemView.context.getString(meItem.name)
        }
    }
}
