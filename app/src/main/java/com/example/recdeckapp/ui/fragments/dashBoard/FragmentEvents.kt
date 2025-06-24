package com.example.recdeckapp.ui.fragments.dashBoard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.EventDetailAdapter
import com.example.recdeckapp.adapter.EventsAdapter
import com.example.recdeckapp.data.entities.EventType
import com.example.recdeckapp.databinding.EventsFragmentBinding
import com.example.recdeckapp.ui.BottomSheet.FilterBottomSheet
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.ui.activities.EventDetailActivity
import com.example.recdeckapp.ui.activities.GroupCreationActivity
import com.example.recdeckapp.ui.activities.NotificationActivity
import com.example.recdeckapp.ui.activities.ProfileActivity
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.viewmodel.EventsViewModel
import com.example.recdeckapp.viewmodel.GroupCreationViewModel

class FragmentEvents : Fragment(R.layout.events_fragment) {
    private var _binding: EventsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventsViewModel by viewModels()
    private lateinit var categoryAdapter: EventsAdapter
    private lateinit var eventAdapter: EventDetailAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = EventsFragmentBinding.bind(view)
        setupAdapters()
        observeViewModel()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.ivProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.ivMenu.setOnClickListener {
            val role = SessionManager.getUserRole(requireContext())
            val userId = SessionManager.getUserId(requireContext())
            Log.e("role", "setOnClickListener: $role")
            Log.e("role", "setOnClickListener: $userId")
        }
        binding.ivNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }
        binding.ivFilter.setOnClickListener {
            showFilterBottomSheet()
        }
        binding.tvCreateEvent.setOnClickListener {
            val userId = SessionManager.getUserId(requireContext())
            handleEventCreationClick(userId)
        }
    }

    private val groupCreationViewModel: GroupCreationViewModel by viewModels()
    private fun handleEventCreationClick(userId: Int) {
        groupCreationViewModel.isGroupAlreadyCreated(userId) { isCreated ->
            if (isCreated) {
                // User has created a group, go to EventCreationActivity
                val intent = Intent(requireContext(), EventCreationActivity::class.java)
                startActivity(intent)
            } else {
                // User has NOT created any group, go to GroupCreationActivity
                val intent = Intent(requireContext(), GroupCreationActivity::class.java)
                intent.putExtra("startFromSecondFragment", false)
                startActivity(intent)
            }
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheet = FilterBottomSheet() // Your FilterBottomSheet class
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun setupAdapters() {
        // Setup category adapter
        categoryAdapter = EventsAdapter(EventType.entries) {
            viewModel.selectEventType(it)
        }
        binding.rvEvents.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        // Observe selectedEventType and set up eventAdapter accordingly
        viewModel.selectedEventType.observe(viewLifecycleOwner) { selectedType ->
            //eventAdapter = EventDetailAdapter(selectedType)
            eventAdapter = EventDetailAdapter(selectedType) {
                val intent = Intent(requireContext(), EventDetailActivity::class.java)
                startActivity(intent)
            }
            // Create new adapter with selected type
            binding.rvEventDetails.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = eventAdapter
            }
            // Also observe and submit updated data, observer to update view
            viewModel.selectedEvents.observe(viewLifecycleOwner) { events ->
                eventAdapter.submitList(events)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.selectedEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}
