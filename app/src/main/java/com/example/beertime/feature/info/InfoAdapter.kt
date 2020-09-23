package com.example.beertime.feature.info

import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beertime.R
import com.example.beertime.models.InfoDto
import kotlinx.android.synthetic.main.item_info.view.*

class InfoAdapter(private val infoDtos: List<InfoDto>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InfoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_info, parent, false )
        )
    }

    override fun getItemCount(): Int = infoDtos.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return (holder as InfoViewHolder).bind(infoDtos[position], position)
    }

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(infoDto: InfoDto, position: Int) {
            if (position % 2 == 0) {
                setLeftWeightedInfo(infoDto)
            } else {
                setRightWeightedInfo(infoDto)
            }

            itemView.tvInfoText.movementMethod = LinkMovementMethod.getInstance()
            itemView.tvInfoText.setLinkTextColor(Color.BLUE)
        }

        private fun setLeftWeightedInfo(infoDto: InfoDto) {
            itemView.tvInfoLeft.text = infoDto.title
            itemView.ivInfoRight.setImageDrawable(itemView.context.getDrawable(infoDto.iconId))
            itemView.tvInfoText.text = infoDto.infoText

            itemView.tvInfoLeft.visibility = View.VISIBLE
            itemView.ivInfoRight.visibility = View.VISIBLE
        }

        private fun setRightWeightedInfo(infoDto: InfoDto) {
            itemView.tvInfoRight.text = infoDto.title
            itemView.ivInfoLeft.setImageDrawable(itemView.context.getDrawable(infoDto.iconId))
            itemView.tvInfoText.text = infoDto.infoText

            itemView.tvInfoRight.visibility = View.VISIBLE
            itemView.ivInfoLeft.visibility = View.VISIBLE

        }
    }
}
