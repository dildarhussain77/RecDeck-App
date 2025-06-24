package com.example.recdeckapp.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recdeckapp.R
import com.example.recdeckapp.data.roomDatabase.entities.PitchCreation.PitchEntity
import com.example.recdeckapp.databinding.ItemPitchesInfoBinding

class PitchesListAdapter(
    val pitchList2: List<PitchEntity>,
    private val onItemClick: (PitchEntity) -> Unit,
    private val preselectedPitchId: Int = -1, // Add this parameter
    private val onDetailClick: (PitchEntity) -> Unit,
    private val onDeleteClick: (PitchEntity) -> Unit,
    private val shouldHideDeleteButton: Boolean = false
) : RecyclerView.Adapter<PitchesListAdapter.PitchViewHolder>() {
    private var selectedPitchId = preselectedPitchId
    val pitchList = pitchList2.toMutableList() //  Internally mutable
    fun removePitch(pitch: PitchEntity): Int {
        val index = pitchList.indexOfFirst { it.pitchId == pitch.pitchId }
        if (index != -1) {
            pitchList.removeAt(index)
        }
        return index
    }

    inner class PitchViewHolder(private val binding: ItemPitchesInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pitch: PitchEntity) {
            with(binding) {
                binding.tvPitchesName.text = pitch.pitchName
                tvPitchesPrice.text = "Rs 2500/hour"
                tvPitchesLocation.text = "Sport Complex,Ajman Dubai"
                tvPitchesTimeShow.text = "${pitch.pitchStartTime} - ${pitch.pitchEndTime}"
                val imageUri = pitch.pitchDocPaths.firstOrNull()
                    ?.let { Uri.parse(Uri.decode(it)) }
                Glide.with(root.context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_alert)
                    .into(ivPitchesImage)
                Log.e("PitchImage", "after:$imageUri ")
                Log.e("CLICK", "ADAPTER: 53")
                if (pitch.isPitchAvailable) {
                    Log.e("CLICK", "ADAPTER: 57")
                    tvNotAvailable.visibility = View.GONE
                    tvAvailable.visibility = View.VISIBLE
                    btnViewDetails.visibility = View.VISIBLE
                    root.isEnabled = true
                } else {
                    Log.e("CLICK", "ADAPTER: 64")
                    tvNotAvailable.visibility = View.VISIBLE
                    tvAvailable.visibility = View.GONE
                    btnViewDetails.visibility = View.GONE
                    root.isEnabled = false
                }
                root.isSelected = pitch.pitchId == selectedPitchId
                if (pitch.pitchId == selectedPitchId) {
                    root.setBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.primary_main)
                    )
                    root.background =
                        ContextCompat.getDrawable(root.context, R.drawable.group_info_bg)
                } else {
                    root.background =
                        ContextCompat.getDrawable(root.context, R.drawable.group_info_bg)
                }
                btnViewDetails.setOnClickListener { onDetailClick(pitch) }
                root.setOnClickListener {
                    onItemClick(pitch)
                    Log.e("CLICK", "ADAPTER: 85")
                }
                btnDelete.visibility = if (shouldHideDeleteButton) View.GONE else View.VISIBLE
                btnDelete.setOnClickListener {
                    Log.d(
                        "PitchDebugs",
                        "Delete button clicked - Availability: ${pitch.isPitchAvailable}"
                    )
                    onDeleteClick(pitch)
                }
                tvPitchesLocation.isSelected = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PitchViewHolder {
        val binding =
            ItemPitchesInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PitchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PitchViewHolder, position: Int) {
        holder.bind(pitchList[position])
    }

    fun updateSelectedPitch(pitchId: Int) {
        selectedPitchId = pitchId
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = pitchList.size
}
