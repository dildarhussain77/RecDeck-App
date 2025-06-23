package com.example.recdeckapp.ui.BottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.recdeckapp.databinding.FilterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false //Prevents closing on outside tap/back press
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.ivClose.setOnClickListener {
            dismiss() // Properly closes the BottomSheet
        }
        binding.ivBackArrowFilterSheet.setOnClickListener {
            dismiss() // Properly closes the BottomSheet
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // Optional: Make it skip collapsed state completely if you want
            behavior.skipCollapsed = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}