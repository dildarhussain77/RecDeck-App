package com.example.recdeckapp.ui.fragments.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentAttachDocsBinding
import com.example.recdeckapp.ui.activities.SignupActivity


class AttachDocsFragment : Fragment(R.layout.fragment_attach_docs) {

    private var _binding: FragmentAttachDocsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding
        _binding = FragmentAttachDocsBinding.bind(view)

        binding.ivBackArrowAttachDocs.bringToFront()
        binding.ivBackArrowAttachDocs.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAttachDocsContinue.setOnClickListener {
            (activity as SignupActivity).switchFragment(AttachDocsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to avoid memory leaks
        _binding = null
    }
}
