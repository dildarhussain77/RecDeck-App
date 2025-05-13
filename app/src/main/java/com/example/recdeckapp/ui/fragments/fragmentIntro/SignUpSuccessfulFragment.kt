package com.example.recdeckapp.ui.fragments.fragmentIntro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentSignUpSuccessfulBinding
import com.example.recdeckapp.ui.activities.SignupActivity

class SignUpSuccessfulFragment : Fragment(R.layout.fragment_sign_up_successful) {

    private var _binding: FragmentSignUpSuccessfulBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpSuccessfulBinding.bind(view)

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
