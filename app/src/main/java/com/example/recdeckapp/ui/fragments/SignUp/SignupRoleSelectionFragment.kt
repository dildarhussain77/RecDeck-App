package com.example.recdeckapp.ui.fragments.SignUp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentSignupRoleSelectionBinding
import com.example.recdeckapp.ui.activities.LoginActivity
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.data.userRole.UserRole
import com.example.recdeckapp.utils.BackPressHelper
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.SignupDataViewModel
//import com.example.recdeckapp.viewmodel.SignupViewModel


class SignupRoleSelectionFragment : Fragment(R.layout.fragment_signup_role_selection) {

    private var _binding: FragmentSignupRoleSelectionBinding? = null
    private val binding get() = _binding!!
//    private lateinit var signupViewModel: SignupViewModel
    private lateinit var signupDataViewModel: SignupDataViewModel
    private var selectedCard = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupRoleSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        signupViewModel = (activity as SignupActivity).signupViewModel
        signupDataViewModel = (activity as SignupActivity).signupDataViewModel

        BackPressHelper.handleBackPress(this, binding.ivBackArrowUserType)
        roleSelectionHandling()
        continueButtonHandling()

        binding.tvLoginClick.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun roleSelectionHandling(){
        // Organizer Card Click
        binding.cardOrganizer.setOnClickListener {
            selectedCard = 1
            signupDataViewModel.selectedRole = UserRole.ORGANIZER // Add this
            updateSelection()
        }

        // Facility Card Click
        binding.cardFacility.setOnClickListener {
            selectedCard = 2
            signupDataViewModel.selectedRole = UserRole.FACILITY
            updateSelection()
        }
    }

    private fun continueButtonHandling(){
        // Initially disable the continue button
        binding.btnSignUpUserSelectContinue.isEnabled = false
        binding.btnSignUpUserSelectContinue.alpha = 0.5f
        // Move to Form 1 on button click using binding
        binding.btnSignUpUserSelectContinue.setOnClickListener {
            Log.e("SignupProfile", "continueButtonHandling: 75 selected role:${signupDataViewModel.selectedRole} ", )
            (activity as FragmentActivity).switchFragment(R.id.signupFragmentContainer, SignupBasicInfoFragment())
        }
    }

    // Update selection and UI
    private fun updateSelection() {
        when (selectedCard) {
            1 -> {
                binding.tickOrganizer.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tick_selected)
                )
                binding.tickFacility.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tick_unselected)
                )
                binding.cardOrganizer.foreground = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_card_selected
                )
                binding.cardFacility.foreground = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_card_default
                )
            }
            2 -> {
                binding.tickOrganizer.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tick_unselected)
                )
                binding.tickFacility.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.tick_selected)
                )
                binding.cardOrganizer.foreground = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_card_default
                )
                binding.cardFacility.foreground = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_card_selected
                )
            }
        }

        // Enable or disable the continue button based on selection
        binding.btnSignUpUserSelectContinue.isEnabled = selectedCard != 0
        binding.btnSignUpUserSelectContinue.alpha = if (selectedCard != 0) 1.0f else 0.5f
    }

    override fun onResume() {
        super.onResume()
        updateSelection()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
