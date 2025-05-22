package com.example.recdeckapp.ui.fragments.SignUp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.CardAdapterIntrest
import com.example.recdeckapp.data.UserRole
import com.example.recdeckapp.data.entities.CardItemIntrests
import com.example.recdeckapp.databinding.FragmentSignupInterestSelectionBinding
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.viewmodel.SignupViewModel

class SignupInterestSelectionFragment : Fragment(R.layout.fragment_signup_interest_selection) {

    private var _binding: FragmentSignupInterestSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardAdapter: CardAdapterIntrest

    private val PREFS_NAME = "user_data"
    private val SELECTED_INTERESTS_KEY = "selected_interests"

    private lateinit var signupViewModel: SignupViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentSignupInterestSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get ViewModel from Activity
        signupViewModel = (activity as SignupActivity).signupViewModel

        // Observe user role from ViewModel
        signupViewModel.selectedRole.observe(viewLifecycleOwner) { role ->
            when (role) {
                UserRole.ORGANIZER -> {
                    Toast.makeText(requireContext(), "For ORGANIZER", Toast.LENGTH_SHORT).show()
                    // You can also do specific logic here if needed
                }
                UserRole.FACILITY -> {
                    Toast.makeText(requireContext(), "For FACILITY", Toast.LENGTH_SHORT).show()
                    // Same here
                }
                else -> {
                    Toast.makeText(requireContext(), "No role selected", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Initialize binding
        _binding = FragmentSignupInterestSelectionBinding.bind(view)

        binding.ivBackArrowSignUp3.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Sample data
        val itemList = listOf(
            CardItemIntrests(R.drawable.basket_ball, "BasketBall"),
            CardItemIntrests(R.drawable.table_tennis, "Table Tennis"),
            CardItemIntrests(R.drawable.horse_ride, "Horse Ride"),
            CardItemIntrests(R.drawable.squash, "Squash"),
            CardItemIntrests(R.drawable.cricket, "Cricket"),
            CardItemIntrests(R.drawable.hockey, "Hockey"),
            CardItemIntrests(R.drawable.cycling, "Cycling"),
            CardItemIntrests(R.drawable.football, "Football"),
            CardItemIntrests(R.drawable.golf, "Golf"),
            CardItemIntrests(R.drawable.swimming_pool, "Swimming pool"),
        )

        // Load saved interests and mark them as selected
        val savedInterests = loadSelectedInterests()
        itemList.forEach { item ->
            item.isSelected = item.title in savedInterests
        }


        // Initialize adapter
        cardAdapter = CardAdapterIntrest(itemList,requireContext())

        // Optional: Listen for selection changes
        cardAdapter.setOnSelectionChangedListener { selectedItems ->
            // Update UI based on selections if needed
            updateSelectionCounter(selectedItems.size)
        }

        // Setting up RecyclerView with GridLayoutManager
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        binding.recyclerView.adapter = cardAdapter

        binding.btnSignUpForm3Continue.setOnClickListener {
            // Get selected interests
            val selectedInterests = cardAdapter.getSelectedItems()

            // Validate selection if needed
            if (selectedInterests.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one interest", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save selected interests to pass to the next screen
            saveSelectedInterests(selectedInterests)

            // 🔥 Observe current role and navigate accordingly
            signupViewModel.selectedRole.value?.let { role ->
                when (role) {
                    UserRole.ORGANIZER -> {
                        (activity as SignupActivity).switchFragment(SignupSuccessFragment())
                    }
                    UserRole.FACILITY -> {
                        (activity as SignupActivity).switchFragment(SignupDocumentUploadFragment())
                    }
                    else -> {
                        Toast.makeText(requireContext(), "No valid role selected!", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                Toast.makeText(requireContext(), "Role is null!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSelectionCounter(count: Int) {
        // Optional: Update a counter or text that shows how many items are selected
//        binding.btnSignUpForm3Continue.text = if (count > 0) {
//            "Continue with $count selected"
//        } else {
//            "Continue"
//        }
    }


    private fun saveSelectedInterests(selectedInterests: List<CardItemIntrests>) {
        val selectedTitles = selectedInterests.map { it.title }.toSet()
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(SELECTED_INTERESTS_KEY, selectedTitles).apply()
    }

    private fun loadSelectedInterests(): Set<String> {
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(SELECTED_INTERESTS_KEY, emptySet()) ?: emptySet()
    }

    override fun onResume() {
        super.onResume()
        // Reload saved interests to maintain selection state
        val savedInterests = loadSelectedInterests()
        cardAdapter.getSelectedItems().forEach { item ->
            item.isSelected = item.title in savedInterests
        }
        cardAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to avoid memory leaks
        _binding = null
    }
}