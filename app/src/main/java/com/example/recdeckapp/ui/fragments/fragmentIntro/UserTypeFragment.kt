package com.example.recdeckapp.ui.fragments.fragmentIntro


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentUserTypeBinding
import com.example.recdeckapp.ui.activities.SignupActivity

class UserTypeFragment : Fragment(R.layout.fragment_user_type) {

    private var _binding: FragmentUserTypeBinding? = null
    private val binding get() = _binding!!

    private var selectedCard = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding
        _binding = FragmentUserTypeBinding.bind(view)

        // Initially disable the continue button
        binding.btnSignUpUserSelectContinue.isEnabled = false
        binding.btnSignUpUserSelectContinue.alpha = 0.5f


        // Organizer Card Click
        binding.cardOrganizer.setOnClickListener {
            selectedCard = 1
            updateSelection()
        }

        // Facility Card Click
        binding.cardFacility.setOnClickListener {
            selectedCard = 2
            updateSelection()
        }

        // Move to Form 1 on button click using binding
        binding.btnSignUpUserSelectContinue.setOnClickListener {
            (activity as SignupActivity).switchFragment(SignUpForm1Fragment())
        }
    }

    // Update selection and UI
    private fun updateSelection() {
        when (selectedCard) {
            1 -> {
                // Organizer selected
                binding.tickOrganizer.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tick_selected)
                )
                binding.tickFacility.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tick_unselected)
                )
            }
            2 -> {
                // Facility selected
                binding.tickOrganizer.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tick_unselected)
                )
                binding.tickFacility.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tick_selected)
                )
            }
        }

        // Enable the continue button
        binding.btnSignUpUserSelectContinue.isEnabled = true
        binding.btnSignUpUserSelectContinue.alpha = 1.0f
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to avoid memory leaks
        _binding = null
    }
}
