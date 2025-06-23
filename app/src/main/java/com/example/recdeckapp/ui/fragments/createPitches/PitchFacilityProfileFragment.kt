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
import androidx.fragment.app.FragmentActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentPitchFacilityProfileBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.PitchCreationActivity
import com.example.recdeckapp.ui.fragments.baseFragment.BaseMediaFragment
import com.example.recdeckapp.utils.MediaPickerUtils
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.PitchCreationViewModel

class PitchFacilityProfileFragment : BaseMediaFragment() {

    private var _binding: FragmentPitchFacilityProfileBinding? = null
    private val binding get() = _binding!!
    private var isValid = false
    private var imageUri: Uri? = null
    private lateinit var pitchCreationViewModel: PitchCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentPitchFacilityProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPitchFacilityProfileBinding.bind(view)

        pitchCreationViewModel = (activity as PitchCreationActivity).pitchCreationViewModel


        val activity = requireActivity() as? PitchCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(3)
        activity?.updateTopBarForFragment(2)

        setOnClickListener()
        setFieldFocusListeners()
        setupFieldListeners()
        updateButtonState()

    }

    private fun setOnClickListener() {

        // Open Bottom Dialog on Camera Icon Click
        binding.cameraIconPitch.setOnClickListener {
            showMediaPickerBottomSheet(
                showCamera = true,
                showGallery = true,
                showFilePicker = false
            )
        }

        binding.btnFacilityProfileContinue.setOnClickListener {
            if (validateAllFields()) {
                pitchCreationViewModel.pitchFacilityName =
                    binding.etFacilityName.text.toString().trim()
                pitchCreationViewModel.pitchFacilityImageUrl = imageUri?.toString()
                Log.e(
                    "PitchCreation",
                    "user id=${pitchCreationViewModel.creatorUserId}," +
                            "pitchFacilityName=${pitchCreationViewModel.pitchFacilityName}," +
                            "pitchFacilityImageUrl=${pitchCreationViewModel.pitchFacilityImageUrl},"
                )
                (activity as FragmentActivity).switchFragment(
                    R.id.PitchCreationFragmentContainer,
                    PitchFacilityDocumentsFragment()
                )
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
                // Handle image selection
                imageUri = uri
                binding.ivFacilityProfilePicture.setImageURI(uri)
                updateButtonState()
            },
            onFileSelected = { uri ->
                // Not needed in this fragment
            }
        )
    }

    private fun setFieldFocusListeners() {
        val fields = listOf(
            binding.etFacilityName,
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

            R.id.etFacilityName -> {
                val text = binding.etFacilityName.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter the Facility Name")
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
        val facilityName = binding.etFacilityName.text.toString().trim()
        if (facilityName.isEmpty()) {
            binding.etFacilityName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }

        return isAllValid
    }

    private fun resetFieldBackgrounds() {
        val normalBackground = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        binding.etFacilityName.background = normalBackground
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etFacilityName.addTextChangedListener(textWatcher)

    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("SignupBasicInfoFragment", "updateButtonState:$isValid")
        binding.btnFacilityProfileContinue.isEnabled = isValid
        binding.btnFacilityProfileContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val facilityName = binding.etFacilityName.text.toString().trim()
        val imageS = binding.ivFacilityProfilePicture.setImageURI(imageUri).toString().trim()
        return facilityName.isNotEmpty()
        //&& imageS
    }

    override fun onResume() {
        super.onResume()
        // Check if image is already selected and show it again
        if (imageUri != null) {
            binding.ivFacilityProfilePicture.setImageURI(imageUri) // update with your imageView id
        }
        setFieldFocusListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }


}