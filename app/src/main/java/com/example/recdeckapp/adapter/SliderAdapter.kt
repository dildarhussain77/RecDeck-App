package com.example.reckdeck.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recdeckapp.databinding.ItemSliderBinding

class SliderAdapter(private val images: List<Any>) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(val binding: ItemSliderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val item = images[position]
        val context = holder.itemView.context

        when (item) {
            is Int -> {
                // Drawable resource
                holder.binding.imageView.setImageResource(item)
            }

            is String -> {
                // URI or URL
                Glide.with(context)
                    .load(Uri.parse(item))
                    .placeholder(android.R.color.darker_gray)
                    .into(holder.binding.imageView)
            }

            else -> {
                // Default fallback
                holder.binding.imageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    override fun getItemCount(): Int = images.size
}
