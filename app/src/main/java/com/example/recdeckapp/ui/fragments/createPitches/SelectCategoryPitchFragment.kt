package com.example.recdeckapp.ui.fragments.createPitches

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
import com.example.recdeckapp.databinding.FragmentSelectCategoryPitchBinding
import com.example.recdeckapp.ui.activities.PitchCreationActivity
import com.example.recdeckapp.utils.InterestItemsProvider
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.PitchCreationViewModel

class SelectCategoryPitchFragment : Fragment() {
    private var _binding: FragmentSelectCategoryPitchBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardAdapter: CardAdapterIntrest
    private lateinit var pitchCreationViewModel: PitchCreationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        _binding = FragmentSelectCategoryPitchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectCategoryPitchBinding.bind(view)
        pitchCreationViewModel = (activity as PitchCreationActivity).pitchCreationViewModel
        val activity = requireActivity() as? PitchCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(1) // example: if it's step 2
        activity?.updateTopBarForFragment(0)
        setOnClickListener()
        // Sample data
        val itemList = InterestItemsProvider.getDefaultInterestItems()
        // Initialize adapter
        cardAdapter = CardAdapterIntrest(
            items = itemList,
            context = requireContext()
        )
        cardAdapter.setOnSelectionChangedListener { selectedItems ->
//
            pitchCreationViewModel.selectedInterests = selectedItems.map {
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
        binding.rvSelectCategoryPitches.layoutManager =
            GridLayoutManager(requireContext(), 2) // 2 columns
        binding.rvSelectCategoryPitches.adapter = cardAdapter
    }

    private fun setOnClickListener() {
        binding.btnSelectCategoryPitchContinue.setOnClickListener {
            pitchCreationViewModel.creatorUserId = SessionManager.getUserId(requireContext())
            Log.e(
                "PitchCreation",
                "user id=${pitchCreationViewModel.creatorUserId}," +
                        "selectedInterests=${pitchCreationViewModel.selectedInterests},"
            )
            (activity as FragmentActivity).switchFragment(
                R.id.PitchCreationFragmentContainer, PitchFacilityDetailsFragment()
            )
        }
    }

    private fun updateSelectionCounter(count: Int) {
        // Optional: Update a counter or text that shows how many items are selected
        binding.btnSelectCategoryPitchContinue.text = if (count > 0) {
            "Continue with $count selected"
        } else {
            "Continue"
        }
    }

    override fun onResume() {
        super.onResume()
        // Restore previously selected interests from ViewModel
        val selectedInterests = pitchCreationViewModel.selectedInterests.map { it.name }
        cardAdapter.items.forEach { item ->
            item.isSelected = selectedInterests.contains(item.title)
        }
        cardAdapter.notifyDataSetChanged()
        updateSelectionCounter(pitchCreationViewModel.selectedInterests.size)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}