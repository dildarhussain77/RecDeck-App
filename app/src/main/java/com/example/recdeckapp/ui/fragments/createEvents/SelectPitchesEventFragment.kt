package com.example.recdeckapp.ui.fragments.createEvents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.PitchesListAdapter
import com.example.recdeckapp.databinding.FragmentSelectPitchesEventBinding
import com.example.recdeckapp.ui.BottomSheet.PitchesDetailsBottomSheet
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.EventCreationViewModel
import com.example.recdeckapp.viewmodel.PitchCreationViewModel

class SelectPitchesEventFragment : Fragment() {
    private var _binding: FragmentSelectPitchesEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var pitchesListAdapter: PitchesListAdapter
    private lateinit var eventCreationViewModel: EventCreationViewModel
    private lateinit var pitchCreationViewModel: PitchCreationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitchCreationViewModel = ViewModelProvider(this).get(PitchCreationViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentSelectPitchesEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectPitchesEventBinding.bind(view)

        eventCreationViewModel = (activity as EventCreationActivity).eventCreationViewModel

        val activity = requireActivity() as? EventCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(4)
        activity?.updateTopBarForFragment(3)

        val userId = SessionManager.getUserId(requireContext())
        eventCreationViewModel.getUsedPitchIds { usedIds ->

            pitchCreationViewModel.getAllUserPitches(userId) { allPitches ->

                val updatedPitches = allPitches.map { pitch ->
                    if (usedIds.contains(pitch.pitchId) &&
                        (!eventCreationViewModel.isEditing || pitch.pitchId != eventCreationViewModel.pitchId)
                    ) {
                        pitch.copy(isPitchAvailable = false)

                    } else pitch
                }

                requireActivity().runOnUiThread {
                    pitchesListAdapter = PitchesListAdapter(
                        pitchList2 = updatedPitches,
                        preselectedPitchId = if (eventCreationViewModel.isEditing)
                            eventCreationViewModel.pitchId else -1,
                        onItemClick = { selectedPitch ->
                            Log.e("CLICK", "onViewCreated: 74")

                            if (selectedPitch.isPitchAvailable ||
                                (eventCreationViewModel.isEditing &&
                                        selectedPitch.pitchId == eventCreationViewModel.pitchId)
                            ) {
                                Log.e("CLICK", "onViewCreated: 79")
                                eventCreationViewModel.pitchId = selectedPitch.pitchId
                                (activity as FragmentActivity).switchFragment(
                                    R.id.EventCreationFragmentContainer,
                                    SelectGroupsEventFragment()
                                )
                            }
                        },
                        onDetailClick = { selectedPitch ->
                            Log.d("PitchDebug", "Selected pitch: ${selectedPitch.pitchName}")
                            pitchCreationViewModel.setSelectedPitch(selectedPitch)

                            val sheet = PitchesDetailsBottomSheet()
                            sheet.setSelectedPitchManually(selectedPitch)
                            sheet.show(parentFragmentManager, "PitchesDetails")

                        },
                        onDeleteClick = {

                        },
                        shouldHideDeleteButton = true

                    )

                    binding.rvPitchesList.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = pitchesListAdapter
                    }

                    if (eventCreationViewModel.isEditing && eventCreationViewModel.pitchId != -1) {
                        val selectedIndex = updatedPitches.indexOfFirst {
                            it.pitchId == eventCreationViewModel.pitchId
                        }
                        if (selectedIndex != -1) {
                            binding.rvPitchesList.scrollToPosition(selectedIndex)
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

        if (::pitchesListAdapter.isInitialized) {
            pitchesListAdapter.updateSelectedPitch(eventCreationViewModel.pitchId)

            val selectedIndex = pitchesListAdapter.pitchList.indexOfFirst {
                it.pitchId == eventCreationViewModel.pitchId
            }
            if (selectedIndex != -1) {
                binding.rvPitchesList.scrollToPosition(selectedIndex)
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}