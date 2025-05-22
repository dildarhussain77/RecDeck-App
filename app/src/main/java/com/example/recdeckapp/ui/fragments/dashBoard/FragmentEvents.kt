package com.example.recdeckapp.ui.fragments.dashBoard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.EventDetailAdapter
import com.example.recdeckapp.adapter.EventsAdapter
import com.example.recdeckapp.data.entities.EventType
import com.example.recdeckapp.databinding.EventsFragmentBinding
import com.example.recdeckapp.viewmodel.EventsViewModel
import androidx.fragment.app.viewModels
import com.example.recdeckapp.ui.BottomSheet.FilterBottomSheet
import com.example.recdeckapp.ui.activities.EventDetailActivity

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
        binding.ivFilter.setOnClickListener {
            showFilterBottomSheet()
        }


    }

    private fun showFilterBottomSheet() {
        val bottomSheet = FilterBottomSheet() // Your FilterBottomSheet class
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }


    private fun setupAdapters() {
        categoryAdapter = EventsAdapter(EventType.values().toList()) {
            viewModel.selectEventType(it)
        }
        //eventAdapter = EventDetailAdapter()

        eventAdapter = EventDetailAdapter {  ->
            val intent = Intent(requireContext(), EventDetailActivity::class.java)

            startActivity(intent)
        }
        binding.rvEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        binding.rvEventDetails.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
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
