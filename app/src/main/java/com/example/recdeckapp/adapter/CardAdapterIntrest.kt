package com.example.recdeckapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.recdeckapp.R
import com.example.recdeckapp.dataClass.Cards.CardItemIntrests
class CardAdapterIntrest(private val items: List<CardItemIntrests>) :
    RecyclerView.Adapter<CardAdapterIntrest.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewIntrests)
        val titleText: TextView = itemView.findViewById(R.id.titleTextIntrests)
        val cardView: CardView = itemView.findViewById(R.id.cardViewIntrests)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_intrests, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]

        holder.imageView.setImageResource(item.image)
        holder.titleText.text = item.title

        // Update border color based on selection
        holder.cardView.setCardBackgroundColor(
            if (item.isSelected)
                holder.itemView.context.getColor(R.color.primary_main)
            else
                holder.itemView.context.getColor(R.color.white_light)
        )

        // Toggle selection on click
        holder.cardView.setOnClickListener {
            item.isSelected = !item.isSelected
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
