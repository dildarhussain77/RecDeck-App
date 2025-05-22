package com.example.recdeckapp.ui.fragments.SignUp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentSignupSuccessBinding

class SignupSuccessFragment : Fragment(R.layout.fragment_signup_success) {

    private var _binding: FragmentSignupSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupSuccessBinding.bind(view)

        binding.ivBackArrowSignUpSuccess.bringToFront()
        binding.ivBackArrowSignUpSuccess.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Button click listener
        binding.btnSignSucessfulUpContinue.setOnClickListener {
            Toast.makeText(requireContext(), "Sign Up Successful!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()  // Close activity after submission
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}
