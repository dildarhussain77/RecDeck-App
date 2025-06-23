package com.example.recdeckapp.ui.fragments.dashBoard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recdeckapp.adapter.GroupListAdapter
import com.example.recdeckapp.databinding.FragmentGroupsBinding
import com.example.recdeckapp.ui.activities.GroupCreationActivity
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.viewmodel.GroupCreationViewModel

class FragmentGroups : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private lateinit var groupListAdapter: GroupListAdapter
    private lateinit var groupCreationViewModel: GroupCreationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupCreationViewModel = ViewModelProvider(this).get(GroupCreationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGroupsBinding.bind(view)


        val userId = SessionManager.getUserId(requireContext())

        groupCreationViewModel.getAllUserGroups(userId) { groups ->
            Log.d("FragmentGroups", "Received ${groups.size} groups")

            requireActivity().runOnUiThread {
                groupListAdapter = GroupListAdapter(
                    groupList2 = groups,
                    onItemClick = {
                    },
                    onDeleteClick = { selectedGroup ->
                        AlertDialogUtils.showCancelDialog(
                            requireContext(),
                            message = "Are you sure you want to delete this group?",
                            onYesClicked = {
                                groupCreationViewModel.deleteGroup(selectedGroup.groupId) {
                                    val index = groupListAdapter.removeGroup(selectedGroup)
                                    if (index != -1) {
                                        groupListAdapter.notifyItemRemoved(index)
                                        checkEmptyState()
                                    }
                                }
                            },
                        )
                    }
                )
                checkEmptyState()

                binding.rvGroupList.adapter = groupListAdapter
                binding.rvGroupList.layoutManager = LinearLayoutManager(requireContext())


            }
        }
        setOnClickListener()
    }

    private fun setOnClickListener() {

        binding.tvCreateGroup.setOnClickListener {

            groupCreationViewModel.creatorUserId = SessionManager.getUserId(requireContext())
            Log.e(
                "GroupCreation",
                "User ID continue ButtonHandling: 48 user id=${groupCreationViewModel.creatorUserId},"
            )
            Log.d(
                "FragmentGroups",
                "Fetching groups for userId: ${groupCreationViewModel.creatorUserId}"
            )
            val intent = Intent(requireContext(), GroupCreationActivity::class.java)
            intent.putExtra("startFromSecondFragment", true)
            startActivity(intent)

        }
    }

    private fun checkEmptyState() {
        if (groupListAdapter.itemCount == 0) {
            binding.clEmptyGroups.visibility = View.VISIBLE
        } else {
            binding.clEmptyGroups.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}