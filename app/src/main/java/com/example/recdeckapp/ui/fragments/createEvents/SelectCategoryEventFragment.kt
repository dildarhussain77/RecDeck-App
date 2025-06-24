package com.example.recdeckapp.ui.fragments.createEvents

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
import com.example.recdeckapp.databinding.FragmentSelectCategoryEventBinding
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.utils.InterestItemsProvider
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.EventCreationViewModel

class SelectCategoryEventFragment : Fragment() {

    private var _binding: FragmentSelectCategoryEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardAdapter: CardAdapterIntrest
    private lateinit var eventCreationViewModel: EventCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentSelectCategoryEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventCreationViewModel = (activity as EventCreationActivity).eventCreationViewModel

        // Show the step indicator
        val activity = requireActivity() as? EventCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(1)
        activity?.updateTopBarForFragment(0)
        setupClickListeners()

        // Sample data
        val itemList = InterestItemsProvider.getDefaultInterestItems()
        // Initialize adapter
        cardAdapter = CardAdapterIntrest(
            items = itemList,
            context = requireContext()
        )
        // Optional: Listen for selection changes
        cardAdapter.setOnSelectionChangedListener { selectedItems ->
            eventCreationViewModel.selectedInterests = selectedItems.map {
                InterestEntity(
                    categoryId = it.id,
                    name = it.title,
                    iconResId = it.imageResource
                )
            }

            updateSelectionCounter(selectedItems.size)
        }
        binding.rvSelectCategoryEvent.layoutManager =
            GridLayoutManager(requireContext(), 2) // 2 columns
        binding.rvSelectCategoryEvent.adapter = cardAdapter
    }


    private fun setupClickListeners() {
        binding.btnSelectCategoryEventContinue.setOnClickListener {
            eventCreationViewModel.creatorUserId = SessionManager.getUserId(requireContext())
            Log.e(
                "GroupCreation",
                "Categories continue ButtonHandling: 70 selectedInterests=${eventCreationViewModel.selectedInterests},"
            )
            (activity as FragmentActivity).switchFragment(
                R.id.EventCreationFragmentContainer,
                EventDetailsFragment()
            )
        }
    }

    private fun updateSelectionCounter(count: Int) {
        // Optional: Update a counter or text that shows how many items are selected
        binding.btnSelectCategoryEventContinue.text = if (count > 0) {
            "Continue with $count selected"
        } else {
            "Continue"
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d(
            "EditingCheck",
            "onResume - selectedInterests from ViewModel = ${eventCreationViewModel.selectedInterests.map { it.name }}"
        )

        if (::cardAdapter.isInitialized) {
            val selectedInterests = eventCreationViewModel.selectedInterests.map { it.name }

            cardAdapter.items.forEach { item ->
                item.isSelected = selectedInterests.contains(item.title)
            }

            cardAdapter.notifyDataSetChanged()
            updateSelectionCounter(eventCreationViewModel.selectedInterests.size)

            Log.d(
                "EditingCheck",
                "onResume - selectedInterests from ViewModel = ${eventCreationViewModel.selectedInterests.map { it.name }}"
            )

        } else {
            Log.w("SelectCategoryEvent", "cardAdapter not initialized yet in onResume()")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}