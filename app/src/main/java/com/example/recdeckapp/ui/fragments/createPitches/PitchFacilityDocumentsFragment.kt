package com.example.recdeckapp.ui.fragments.createPitches

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentPitchFacilityDocumentsBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.PitchCreationActivity
import com.example.recdeckapp.ui.fragments.baseFragment.BaseMediaFragment
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.FileDisplayUtils
import com.example.recdeckapp.utils.MediaPickerUtils
import com.example.recdeckapp.viewmodel.PitchCreationViewModel

class PitchFacilityDocumentsFragment : BaseMediaFragment() {
    private var _binding: FragmentPitchFacilityDocumentsBinding? = null
    private val binding get() = _binding!!
    private var isValid = false
    private val selectedFiles = mutableListOf<Uri>()
    private lateinit var pitchCreationViewModel: PitchCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentPitchFacilityDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPitchFacilityDocumentsBinding.bind(view)

        pitchCreationViewModel = (activity as PitchCreationActivity).pitchCreationViewModel

        val activity = requireActivity() as? PitchCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(4)
        activity?.updateTopBarForFragment(3)
        setOnClickListener()
        setFieldFocusListeners()
        setupFieldListeners()
        updateButtonState()

    }

    private fun setOnClickListener() {

        binding.uploadLayoutFaciltyPitchIDPass.setOnClickListener {
            if (selectedFiles.size >= 3) {
                (activity as? BaseActivity)?.showToast("more than 3 files can not be upload")
                return@setOnClickListener
            }
            showMediaPickerBottomSheet(
                showCamera = true,
                showGallery = true,
                showFilePicker = false
            )
        }

        binding.btnPitchFacilityDocumentsContinue.alpha = 0.5f
        binding.btnPitchFacilityDocumentsContinue.setOnClickListener {
            binding.btnPitchFacilityDocumentsContinue.isEnabled = false
            if (validateAllFields()) {
                pitchCreationViewModel.pitchIdPass = binding.etIdPassNum.text.toString().trim()
                Log.e(
                    "PitchCreation",
                    "user id=${pitchCreationViewModel.creatorUserId}," +
                            "pitchIdPass=${pitchCreationViewModel.pitchIdPass},"
                )
                // Show loading
                binding.pgBarPitchCreated.visibility = View.VISIBLE
                binding.btnPitchFacilityDocumentsContinue.isEnabled = false

                pitchCreationViewModel.insertPitchToRoom { success ->
                    requireActivity().runOnUiThread {
                        binding.pgBarPitchCreated.visibility = View.GONE

                        if (success) {
                            AlertDialogUtils.showCancelDialog(
                                requireContext(),
                                title = "Success!",
                                message = "Your pitch has been created successfully.",
                                showContinue = true,
                                customIconResId = R.drawable.ic_success,
                                onContinueClicked = {
                                    requireActivity().finish()
                                },
                            )
                        } else {
                            binding.btnPitchFacilityDocumentsContinue.isEnabled = true
                            (activity as? BaseActivity)?.showToast("Failed to Create Pitch")
                        }
                    }
                }
            } else {
                (activity as? BaseActivity)?.showToast("Please fill all..")
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MediaPickerUtils.handleActivityResult(
            requestCode,
            resultCode,
            data,
            requireContext(),
            onImageSelected = { uri ->
                // Handle image selection for documents
                selectedFiles.add(uri)
                pitchCreationViewModel.addDocumentPath(uri.toString())
                Log.e(
                    "PitchCreation",
                    "user id=${pitchCreationViewModel.creatorUserId}," +
                            "addDocumentPath=${pitchCreationViewModel.pitchDocPaths},"
                )
                showSelectedFile(uri)
                updateButtonState()
            },
            onFileSelected = { uri ->
                // Handle file selection
                selectedFiles.add(uri)
                pitchCreationViewModel.addDocumentPath(uri.toString())
                Log.e(
                    "PitchCreation",
                    "user id=${pitchCreationViewModel.creatorUserId}," +
                            "addDocumentPath=${pitchCreationViewModel.pitchDocPaths},"
                )
                showSelectedFile(uri)
                updateButtonState()
            }
        )
    }

    private fun showSelectedFile(uri: Uri) {
        FileDisplayUtils.showSelectedFile(
            uri,
            requireContext(),
            binding.fileContainer
        ) { removedUri ->
            selectedFiles.remove(removedUri)
            pitchCreationViewModel.removeDocumentPath(removedUri.toString())
            Log.e(
                "PitchCreation",
                "user id=${pitchCreationViewModel.creatorUserId}," +
                        "removeDocumentPath=${pitchCreationViewModel.pitchDocPaths},"
            )
            updateButtonState()
            if (binding.fileContainer.childCount == 0) {
                binding.tvUploadDocsHeading.visibility = View.GONE
            }
        }
        binding.tvUploadDocsHeading.visibility = View.VISIBLE
    }

    private fun setFieldFocusListeners() {
        val fields = listOf(
            binding.etIdPassNum,
        )

        for (field in fields) {
            field.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // When field gains focus, set to focused background
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    Log.d("FocusDebug", " field focus changed: $hasFocus")

                } else {
                    // When field loses focus, validate and set appropriate background
                    validateFieldOnFocusLost(view)
                }
            }
        }
    }

    private fun validateFieldOnFocusLost(view: View) {
        when (view.id) {

            R.id.etIdPassNum -> {
                val text = binding.etIdPassNum.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter the ID Number")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }
        }
    }

    private fun validateAllFields(): Boolean {
        var isAllValid = true
        // Reset all field backgrounds first
        resetFieldBackgrounds()

        // Validate pitch Name
        val passNumber = binding.etIdPassNum.text.toString().trim()
        if (passNumber.isEmpty()) {
            binding.etIdPassNum.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }

        return isAllValid
    }

    private fun resetFieldBackgrounds() {
        val normalBackground = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        binding.etIdPassNum.background = normalBackground
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etIdPassNum.addTextChangedListener(textWatcher)

    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("SignupBasicInfoFragment", "updateButtonState:$isValid")
        binding.btnPitchFacilityDocumentsContinue.isEnabled = isValid
        binding.btnPitchFacilityDocumentsContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val passNumber = binding.etIdPassNum.text.toString().trim()
        return passNumber.isNotEmpty()
    }

    override fun onResume() {
        super.onResume()
        if (selectedFiles.isNotEmpty()) {
            binding.fileContainer.removeAllViews()
            selectedFiles.forEach { uri ->
                showSelectedFile(uri)
            }
            updateButtonState()  // update continue button state
        }
        setFieldFocusListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}