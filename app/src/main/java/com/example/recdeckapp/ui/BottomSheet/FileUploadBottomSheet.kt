//package com.example.recdeckapp.ui.BottomSheet
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.recdeckapp.databinding.BottomSheetFileUploadBinding
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//
//class FileUploadBottomSheet(
//    private val onCameraClick: () -> Unit,
//    private val onGalleryClick: () -> Unit
//) : BottomSheetDialogFragment() {
//
//    private var _binding: BottomSheetFileUploadBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = BottomSheetFileUploadBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    // View ready hone ke baad listener lagayein
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setupListeners()
//    }
//
//    private fun setupListeners() {
//        binding.ivClose.setOnClickListener {
//            dismiss()
//        }
//
//        binding.imgCameraBottomDialog.setOnClickListener {
//            onCameraClick()
//            dismiss()
//        }
//
//        binding.imgGalleryBottomDialog.setOnClickListener {
//            onGalleryClick()
//            dismiss()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null // Memory leak prevention
//    }
//}