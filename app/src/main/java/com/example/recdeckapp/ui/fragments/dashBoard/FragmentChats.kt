package com.example.recdeckapp.ui.fragments.dashBoard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recdeckapp.adapter.EventListAdapter
import com.example.recdeckapp.databinding.FragmentChatsBinding
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.viewmodel.EventCreationViewModel

class FragmentChats : Fragment() {
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventListAdapter: EventListAdapter
    private lateinit var eventCreationViewModel: EventCreationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventCreationViewModel = ViewModelProvider(this).get(EventCreationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatsBinding.bind(view)
        loadEventList()
        eventListAdapter = EventListAdapter(
            onItemClick = {
            },
            onEditClick = { event ->
                val intent =
                    Intent(requireContext(), EventCreationActivity::class.java).apply {
                        putExtra("eventId", event.eventId)
                        putExtra("isEditing", true)
                    }
                startActivity(intent)
            },
            onDeleteClick = { event ->
                AlertDialogUtils.showCancelDialog(
                    requireContext(),
                    message = "Are you sure you want to delete this event?",
                    onYesClicked = {
                        eventCreationViewModel.deleteEvent(event.eventId) {
                            val index = eventListAdapter.removeEvent(event)
                            if (index != -1) {
                                eventListAdapter.notifyItemRemoved(index)
                                checkEmptyState()
                            }
                        }
                    },
                )
            }
        )
        binding.rvEventList.adapter = eventListAdapter
        binding.rvEventList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun checkEmptyState() {
        if (eventListAdapter.itemCount == 0) {
            binding.clEmptyEvents.visibility = View.VISIBLE
        } else {
            binding.clEmptyEvents.visibility = View.GONE
        }
    }

    private fun loadEventList() {
        val userId = SessionManager.getUserId(requireContext())
        eventCreationViewModel.getAllUserEvents(userId) { events ->
            requireActivity().runOnUiThread {
                eventListAdapter.submitList(events)
                checkEmptyState()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::eventListAdapter.isInitialized) {
            loadEventList() // This ensures updated events show up instantly
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}