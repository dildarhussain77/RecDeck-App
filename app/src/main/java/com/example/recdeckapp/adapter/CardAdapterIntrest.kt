package com.example.recdeckapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recdeckapp.R
import com.example.recdeckapp.data.entities.CardItemIntrests
import com.google.android.material.card.MaterialCardView

class CardAdapterIntrest(
    var items: List<CardItemIntrests>,
    private val context: Context,
) : RecyclerView.Adapter<CardAdapterIntrest.CardViewHolder>() {
    private var onSelectionChangedListener: ((List<CardItemIntrests>) -> Unit)? = null

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewIntrests)
        val titleText: TextView = itemView.findViewById(R.id.titleTextIntrests)
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardViewIntrests)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_intrests, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.imageResource)
        holder.titleText.text = item.title
        updateCardAppearance(holder.cardView, item.isSelected)
        holder.cardView.setOnClickListener {
            item.isSelected = !item.isSelected
            updateCardAppearance(holder.cardView, item.isSelected)
            // Notify ViewModel through listener
            onSelectionChangedListener?.invoke(getSelectedItems())
            notifyItemChanged(position)
        }
    }

    private fun updateCardAppearance(cardView: MaterialCardView, isSelected: Boolean) {
        if (isSelected) {
            cardView.strokeColor = ContextCompat.getColor(context, R.color.primary_main)
            cardView.strokeWidth =
                context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
        } else {
            cardView.strokeWidth = 0
        }
    }

    override fun getItemCount(): Int = items.size
    fun getSelectedItems(): List<CardItemIntrests> = items.filter { it.isSelected }
    fun setOnSelectionChangedListener(listener: (List<CardItemIntrests>) -> Unit) {
        this.onSelectionChangedListener = listener
    }
}
