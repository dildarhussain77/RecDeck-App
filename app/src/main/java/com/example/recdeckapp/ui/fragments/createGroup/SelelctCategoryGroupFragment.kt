package com.example.recdeckapp.ui.fragments.createGroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.CardAdapterIntrest
import com.example.recdeckapp.data.roomDatabase.entities.CommonEntities.InterestEntity
import com.example.recdeckapp.databinding.FragmentSelelctCategoryGroupBinding
import com.example.recdeckapp.ui.activities.GroupCreationActivity
import com.example.recdeckapp.utils.InterestItemsProvider
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.GroupCreationViewModel


class SelelctCategoryGroupFragment : Fragment() {

    private var _binding: FragmentSelelctCategoryGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardAdapter: CardAdapterIntrest
    private lateinit var groupCreationViewModel: GroupCreationViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentSelelctCategoryGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelelctCategoryGroupBinding.bind(view)

        groupCreationViewModel = (activity as GroupCreationActivity).groupCreationViewModel

        setOnClickListener()

        // Show the step indicator
        val activity = requireActivity() as? GroupCreationActivity
        activity?.showStepIndicator(true)
        // Step indicator pe current step set karo
        activity?.updateStepIndicator(1) // example: if it's step 2
        activity?.updateTopBarForFragment(1)

        // Sample data
        val itemList = InterestItemsProvider.getDefaultInterestItems()

        // Initialize adapter
        cardAdapter = CardAdapterIntrest(
            items = itemList,
            context = requireContext()
        )
        // Optional: Listen for selection changes
        cardAdapter.setOnSelectionChangedListener { selectedItems ->

            groupCreationViewModel.selectedInterests = selectedItems.map {
                InterestEntity(
                    categoryId = it.id,
                    name = it.title,
                    iconResId = it.imageResource
                )
            }
            // Update UI based on selections if needed
            updateSelectionCounter(selectedItems.size)
        }

        // Setting up RecyclerView with GridLayoutManager
        binding.rvSelectCategoryGroup.layoutManager =
            GridLayoutManager(requireContext(), 2) // 2 columns
        binding.rvSelectCategoryGroup.adapter = cardAdapter
    }

    private fun setOnClickListener() {
        binding.btnSelectCategoryGroupContinue.setOnClickListener {
            groupCreationViewModel.creatorUserId = SessionManager.getUserId(requireContext())
            Log.e(
                "GroupCreation",
                "Categories continue ButtonHandling: 70 selectedInterests=${groupCreationViewModel.selectedInterests},"
            )
            Log.e(
                "GroupCreation",
                "Categories continue ButtonHandling: 70 iidddd=${groupCreationViewModel.creatorUserId},"
            )
            (activity as FragmentActivity).switchFragment(
                R.id.GroupCreationFragmentContainer,
                GroupDetailsFragment()
            )
        }
    }

    private fun updateSelectionCounter(count: Int) {
        // Optional: Update a counter or text that shows how many items are selected
        binding.btnSelectCategoryGroupContinue.text = if (count > 0) {
            "Continue with $count selected"
        } else {
            "Continue"
        }
    }

    override fun onResume() {
        super.onResume()
        // Restore previously selected interests from ViewModel
        val selectedInterests = groupCreationViewModel.selectedInterests.map { it.name }

        cardAdapter.items.forEach { item ->
            item.isSelected = selectedInterests.contains(item.title)
        }

        cardAdapter.notifyDataSetChanged()
        updateSelectionCounter(groupCreationViewModel.selectedInterests.size)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}