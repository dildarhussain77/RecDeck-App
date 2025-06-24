package com.example.recdeckapp.ui.fragments.createPitches

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentPitchFacilityDetailsBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.PitchCreationActivity
import com.example.recdeckapp.utils.TextFieldDescUtils
import com.example.recdeckapp.utils.TimePickerUtils
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.PitchCreationViewModel

class PitchFacilityDetailsFragment : Fragment() {
    private var _binding: FragmentPitchFacilityDetailsBinding? = null
    private val binding get() = _binding!!
    private var isValid = false
    private var startHour: Int = -1
    private var startMinute: Int = -1
    private var endHour: Int = -1
    private var endMinute: Int = -1
    private lateinit var pitchCreationViewModel: PitchCreationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        _binding = FragmentPitchFacilityDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPitchFacilityDetailsBinding.bind(view)
        pitchCreationViewModel = (activity as PitchCreationActivity).pitchCreationViewModel
        val activity = requireActivity() as? PitchCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(2)
        activity?.updateTopBarForFragment(1)
        binding.etStartTime.showSoftInputOnFocus = false
        binding.etEndTime.showSoftInputOnFocus = false
        setOnClickListener()
        setFieldFocusListeners()
        setupFieldListeners()
        updateButtonState()
        TimePicker()
        TextFieldDescUtils.setupDescWatcher(
            binding.ettvFacilityDesc,
            binding.tvDescCharCount,
            maxLength = 600
        ) { isValid ->
            //isDescValid = isValid
            //checkFormCompletion()
        }
    }

    private fun setOnClickListener() {
        binding.btnFacilityDetailContinue.setOnClickListener {
            if (validateAllFields()) {
                // Send data to ViewModel
                pitchCreationViewModel.pitchName = binding.etPitchName.text.toString().trim()
                pitchCreationViewModel.pitchStartTime = binding.etStartTime.text.toString().trim()
                pitchCreationViewModel.pitchEndTime = binding.etEndTime.text.toString().trim()
                pitchCreationViewModel.pitchDescription =
                    binding.ettvFacilityDesc.text.toString().trim()
                Log.e(
                    "PitchCreation",
                    "user id=${pitchCreationViewModel.creatorUserId}," +
                            "pitchName=${pitchCreationViewModel.pitchName}," +
                            "pitchStartTime=${pitchCreationViewModel.pitchStartTime}," +
                            "pitchEndTime=${pitchCreationViewModel.pitchEndTime}," +
                            "pitchDescription=${pitchCreationViewModel.pitchDescription},"
                )
                (activity as FragmentActivity).switchFragment(
                    R.id.PitchCreationFragmentContainer,
                    PitchFacilityProfileFragment()
                )
            } else {
                (activity as? BaseActivity)?.showToast("Please fill all fields")
            }
        }
    }

    private fun TimePicker() {
        TimePickerUtils.attachTimePickers(
            context = requireContext(),
            startEditText = binding.etStartTime,
            endEditText = binding.etEndTime
        ) { sH, sM, eH, eM ->
            startHour = sH
            startMinute = sM
            endHour = eH
            endMinute = eM
        }
    }

    private fun setFieldFocusListeners() {
        val fields = listOf(
            binding.etPitchName,
//            binding.etStartTime,
//            binding.etEndTime,
            binding.ettvFacilityDesc
        )
        for (field in fields) {
            field.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // When field gains focus, set to focused background
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    Log.d("FocusDebug", " field focus changed: $hasFocus")
                    if (view.id == R.id.etStartTime) {
                    }
                    if (view.id == R.id.etEndTime) {
                    }
                } else {
                    // When field loses focus, validate and set appropriate background
                    validateFieldOnFocusLost(view)
                }
            }
        }
    }

    private fun validateFieldOnFocusLost(view: View) {
        when (view.id) {
            R.id.etPitchName -> {
                val text = binding.etPitchName.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter the Pitch Name")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.ettvFacilityDesc -> {
                val text = binding.ettvFacilityDesc.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please Enter Pitch Desc")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.etStartTime -> {
                val text = binding.etStartTime.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please Select Start Time")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.etEndTime -> {
                val text = binding.etEndTime.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please Select End Time")
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
        // Validate group Name
        val pitchName = binding.etPitchName.text.toString().trim()
        if (pitchName.isEmpty()) {
            binding.etPitchName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        val Desc = binding.ettvFacilityDesc.text.toString().trim()
        if (Desc.isEmpty()) {
            binding.ettvFacilityDesc.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        val startTime = binding.etStartTime.text.toString().trim()
        if (startTime.isEmpty()) {
            binding.etStartTime.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        val endTime = binding.etEndTime.text.toString().trim()
        if (endTime.isEmpty()) {
            binding.etEndTime.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        return isAllValid
    }

    private fun resetFieldBackgrounds() {
        val normalBackground = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        binding.etPitchName.background = normalBackground
        binding.ettvFacilityDesc.background = normalBackground
        binding.etStartTime.background = normalBackground
        binding.etEndTime.background = normalBackground
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etPitchName.addTextChangedListener(textWatcher)
        binding.ettvFacilityDesc.addTextChangedListener(textWatcher)
        binding.etStartTime.addTextChangedListener(textWatcher)
        binding.etEndTime.addTextChangedListener(textWatcher)
    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("SignupBasicInfoFragment", "updateButtonState:$isValid")
        binding.btnFacilityDetailContinue.isEnabled = isValid
        binding.btnFacilityDetailContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val pitchName = binding.etPitchName.text.toString().trim()
        val desc = binding.ettvFacilityDesc.text.toString().trim()
        val startTime = binding.etStartTime.text.toString().trim()
        val endTime = binding.etEndTime.text.toString().trim()
        return pitchName.isNotEmpty() &&
                desc.isNotEmpty() &&
                startTime.isNotEmpty() &&
                endTime.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}