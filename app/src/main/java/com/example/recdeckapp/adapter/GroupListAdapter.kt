package com.example.recdeckapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recdeckapp.R
import com.example.recdeckapp.data.roomDatabase.entities.GroupCreation.GroupEntity
import com.example.recdeckapp.databinding.ItemGroupInfoBinding

class GroupListAdapter(
    private val groupList2: List<GroupEntity>,
    private val preselectedGroupId: Int? = null,
    private val onItemClick: (GroupEntity) -> Unit,
    private val onDeleteClick: (GroupEntity) -> Unit,
    private val shouldHideDeleteButton: Boolean = false
) :
    RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {
    private var selectedGroupId = preselectedGroupId

    val groupList = groupList2.toMutableList() // Internally mutable

    fun removeGroup(group: GroupEntity): Int {
        val index = groupList.indexOfFirst { it.groupId == group.groupId }
        if (index != -1) {
            groupList.removeAt(index)
        }
        return index
    }

    inner class GroupViewHolder(val binding: ItemGroupInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: GroupEntity) {
            with(binding) {
                tvGroupName.text = group.groupName
                tvDescription.text = group.description
                Glide.with(ivGroupImage.context)
                    .load(group.imageUrl)
                    .placeholder(R.drawable.ic_image)
                    .into(ivGroupImage)

                if (group.isGroupAvailable) {
                    Log.e("CLICK", "ADAPTER: 57")
                    tvNotAvailable.visibility = View.GONE
                    root.isEnabled = true
                    //root.alpha = 1.0f

                } else {
                    Log.e("CLICK", "ADAPTER: 64")
                    tvNotAvailable.visibility = View.VISIBLE
                    root.isEnabled = false
                    //root.alpha = 0.5f
                }

                // Highlight selected group
                root.isSelected = group.groupId == selectedGroupId
                if (group.groupId == selectedGroupId) {
                    root.setBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.primary_main)
                    )
                    root.setBackground(
                        ContextCompat.getDrawable(root.context, R.drawable.group_info_bg)
                    )
                } else {
                    root.setBackground(
                        ContextCompat.getDrawable(root.context, R.drawable.group_info_bg)
                    )
                }
                root.setOnClickListener {
                    Log.e(
                        "GROUP_CLICK",
                        "Clicked group: ${group.groupName}, Available: ${group.isGroupAvailable}"
                    )
                    if (group.isGroupAvailable) {
                        selectedGroupId = group.groupId
                        notifyDataSetChanged() // Refresh selection
                        onItemClick(group)
                        Log.e(
                            "GROUP_CLICK",
                            "Group: ${group.groupName}, isAvailable: ${group.isGroupAvailable}"
                        )
                    } else {
                        Log.e("GROUP_CLICK", "Group is not available, click ignored")
                    }
                }
                btnDelete.visibility = if (shouldHideDeleteButton) View.GONE else View.VISIBLE
                btnDelete.setOnClickListener {
                    onDeleteClick(group)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding =
            ItemGroupInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]
        holder.bind(group)

    }

    override fun getItemCount() = groupList.size
}
