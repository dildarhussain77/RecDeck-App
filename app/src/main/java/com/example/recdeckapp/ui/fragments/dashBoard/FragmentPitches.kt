package com.example.recdeckapp.ui.fragments.dashBoard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recdeckapp.adapter.PitchesListAdapter
import com.example.recdeckapp.databinding.FragmentPitchesBinding
import com.example.recdeckapp.ui.BottomSheet.PitchesDetailsBottomSheet
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.PitchCreationActivity
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.viewmodel.EventCreationViewModel
import com.example.recdeckapp.viewmodel.PitchCreationViewModel

class FragmentPitches : Fragment() {

    private var _binding: FragmentPitchesBinding? = null
    private val binding get() = _binding!!

    private lateinit var pitchesListAdapter: PitchesListAdapter
    private lateinit var eventCreationViewModel: EventCreationViewModel
    private lateinit var pitchCreationViewModel: PitchCreationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitchCreationViewModel = ViewModelProvider(this).get(PitchCreationViewModel::class.java)
        eventCreationViewModel = ViewModelProvider(this).get(EventCreationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentPitchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPitchesBinding.bind(view)

        setOnClickListener()
        loadUserPitches()


    }

    private fun loadUserPitches() {
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

                        },
                        onDetailClick = { selectedPitch ->
                            Log.d("PitchDebug", "Selected pitch: ${selectedPitch.pitchName}")
                            pitchCreationViewModel.setSelectedPitch(selectedPitch)

                            val sheet = PitchesDetailsBottomSheet()
                            sheet.setSelectedPitchManually(selectedPitch)
                            sheet.show(parentFragmentManager, "PitchesDetails")
                        },
                        onDeleteClick = { selectedPitch ->
                            if (!selectedPitch.isPitchAvailable) {
                                Log.d("PitchDebugs", "INSIDE IF BLOCK — Pitch unavailable")
                                Toast.makeText(
                                    requireContext(),
                                    "This pitch is already assigned to an event and cannot be deleted.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                (activity as? BaseActivity)?.showToast("This pitch is already assigned to an event and cannot be deleted.")
                                return@PitchesListAdapter
                            }

                            AlertDialogUtils.showCancelDialog(
                                requireContext(),
                                message = "Are you sure you want to delete this pitch?",
                                onYesClicked = {
                                    pitchCreationViewModel.deletePitch(selectedPitch.pitchId) {
                                        val index = pitchesListAdapter.removePitch(selectedPitch)
                                        if (index != -1) {
                                            pitchesListAdapter.notifyItemRemoved(index)
                                            checkEmptyState()
                                        }
                                    }
                                },
                            )
                        }
                    )

                    binding.rvPitchesList.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = pitchesListAdapter
                    }
                    checkEmptyState()
                }
            }
        }

    }

    private fun setOnClickListener() {

        binding.tvCreatePitch.setOnClickListener {

            pitchCreationViewModel.creatorUserId = SessionManager.getUserId(requireContext())
            Log.e(
                "PitchCreation",
                "User ID continue ButtonHandling: 48 user id=${pitchCreationViewModel.creatorUserId},"
            )
            Log.d(
                "PitchCreation",
                "Fetching groups for userId: ${pitchCreationViewModel.creatorUserId}"
            )
            val intent = Intent(requireContext(), PitchCreationActivity::class.java)
            startActivity(intent)

        }
    }

    private fun checkEmptyState() {
        if (pitchesListAdapter.itemCount == 0) {
            binding.clEmptyPitches.visibility = View.VISIBLE
        } else {
            binding.clEmptyPitches.visibility = View.GONE
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
        loadUserPitches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}