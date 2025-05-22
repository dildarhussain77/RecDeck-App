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


class CardAdapterIntrest(private val items: List<CardItemIntrests>
,
    private val context: Context) :
    RecyclerView.Adapter<CardAdapterIntrest.CardViewHolder>() {

    // Optional: Add a callback for when selections change
    private var onSelectionChangedListener: ((List<CardItemIntrests>) -> Unit)? = null

    // Keep track of selected items (optional enhancement)
    private val selectedItems: MutableList<CardItemIntrests> = mutableListOf()

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

        holder.imageView.setImageResource(item.image)
        holder.titleText.text = item.title

        // Apply the appropriate background based on selection state
        updateCardAppearance(holder.cardView, item.isSelected)

        // Toggle selection on card click
        holder.cardView.setOnClickListener {
            // Toggle selection state
            item.isSelected = !item.isSelected

            // Update selections list
            if (item.isSelected) {
                if (!selectedItems.contains(item)) {
                    selectedItems.add(item)
                }
            } else {
                selectedItems.remove(item)
            }

            // Update visual appearance
            updateCardAppearance(holder.cardView, item.isSelected)

            // Notify about selection change
            onSelectionChangedListener?.invoke(getSelectedItems())
        }
    }

    private fun updateCardAppearance(cardView: MaterialCardView, isSelected: Boolean) {
//        if (isSelected) {
//            cardView.(R.drawable.bg_card_selected)
//        } else {
//            cardView.setBackgroundResource(R.drawable.bg_card_default)
//        }
        if (isSelected)
        {
            cardView.setStrokeColor(ContextCompat.getColor(context, R.color.primary_main))
            cardView.strokeWidth =
                context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
        }
        else{
            cardView.strokeWidth = 0
        }

    }



    override fun getItemCount(): Int = items.size

    // Get currently selected items
    fun getSelectedItems(): List<CardItemIntrests> {
        return items.filter { it.isSelected }
    }

    // Set selection change listener
    fun setOnSelectionChangedListener(listener: (List<CardItemIntrests>) -> Unit) {
        this.onSelectionChangedListener = listener
    }
}