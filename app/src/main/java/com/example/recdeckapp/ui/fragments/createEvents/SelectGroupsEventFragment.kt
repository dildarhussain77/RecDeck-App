package com.example.recdeckapp.ui.fragments.createEvents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.GroupListAdapter
import com.example.recdeckapp.databinding.FragmentSelectGroupsEventBinding
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.EventCreationViewModel
import com.example.recdeckapp.viewmodel.GroupCreationViewModel

class SelectGroupsEventFragment : Fragment() {
    private var _binding: FragmentSelectGroupsEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var groupListAdapter: GroupListAdapter
    private lateinit var groupCreationViewModel: GroupCreationViewModel
    private lateinit var eventCreationViewModel: EventCreationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupCreationViewModel = ViewModelProvider(this).get(GroupCreationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentSelectGroupsEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectGroupsEventBinding.bind(view)

        eventCreationViewModel = (activity as EventCreationActivity).eventCreationViewModel
        val activity = requireActivity() as? EventCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(5)
        activity?.updateTopBarForFragment(4)
        val userId = SessionManager.getUserId(requireContext())

        eventCreationViewModel.getUsedGroupIds { usedGroupIds ->

            groupCreationViewModel.getAllUserGroups(userId) { groups ->
                // 🛠 Step 3: Mark availability based on usage
                val currentGroupId = eventCreationViewModel.groupId
                val updatedGroups = groups.map { group ->
                    val isUsedByOthers =
                        group.groupId in usedGroupIds && group.groupId != currentGroupId
                    group.copy(isGroupAvailable = !isUsedByOthers)
                }
                requireActivity().runOnUiThread {
                    groupListAdapter = GroupListAdapter(
                        groupList2 = updatedGroups,
                        preselectedGroupId = if (eventCreationViewModel.isEditing) eventCreationViewModel.groupId else -1,
                        onItemClick = {
                            if (it.isGroupAvailable) {
                                eventCreationViewModel.groupId = it.groupId
                                (activity as FragmentActivity).switchFragment(
                                    R.id.EventCreationFragmentContainer,
                                    EventPaymentFragment()
                                )
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "This group is already assigned.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onDeleteClick = {},
                        shouldHideDeleteButton = true
                    )

                    binding.rvGroupList.adapter = groupListAdapter
                    binding.rvGroupList.layoutManager = LinearLayoutManager(requireContext())

                    // Scroll to selected group if editing
                    if (eventCreationViewModel.isEditing && eventCreationViewModel.groupId != -1) {
                        val selectedIndex =
                            groups.indexOfFirst { it.groupId == eventCreationViewModel.groupId }
                        if (selectedIndex != -1) {
                            binding.rvGroupList.scrollToPosition(selectedIndex)
                        }
                    }
                }
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}