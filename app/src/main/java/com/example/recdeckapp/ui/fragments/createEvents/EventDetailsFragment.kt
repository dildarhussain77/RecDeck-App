package com.example.recdeckapp.ui.fragments.createEvents

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
import com.example.recdeckapp.databinding.FragmentEventDetailsBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.utils.CustomDatePickerDialog
import com.example.recdeckapp.utils.TimePickerUtils
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.EventCreationViewModel

class EventDetailsFragment : Fragment() {
    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!
    private var isValid = false
    private var startHour: Int = -1
    private var startMinute: Int = -1
    private var endHour: Int = -1
    private var endMinute: Int = -1
    private lateinit var eventCreationViewModel: EventCreationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEventDetailsBinding.bind(view)
        // Access the ViewModel from the Activity scope
        eventCreationViewModel = (activity as EventCreationActivity).eventCreationViewModel
        val activity = requireActivity() as? EventCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(2)
        activity?.updateTopBarForFragment(1)
        binding.etSelectDate.showSoftInputOnFocus = false
        binding.etStartTime.showSoftInputOnFocus = false
        binding.etEndTime.showSoftInputOnFocus = false
        setOnClickListener()
        setFieldFocusListeners()
        setupFieldListeners()
        updateButtonState()
        if (eventCreationViewModel.isEditing) {
            // Pre-populate fields with existing data
            binding.etEventName.setText(eventCreationViewModel.eventName)
            binding.etNoOfEventMem.setText(eventCreationViewModel.memberLimit.toString()) // Fix: convert to String
            binding.etSelectDate.setText(eventCreationViewModel.eventDate)
            binding.etStartTime.setText(eventCreationViewModel.eventStartTime)
            binding.etEndTime.setText(eventCreationViewModel.eventEndTime)
        }
    }

    private fun setOnClickListener() {
        binding.etSelectDate.setOnClickListener {
            CustomDatePickerDialog.show(
                context = requireContext(),
                onDateSelected = { formattedDate, rawMillis ->
                    // Do whatever you want with the date
                    binding.etSelectDate.setText(formattedDate)
                }
            )
        }
        // Set up your click listeners
        binding.etStartTime.setOnClickListener {
            TimePicker()
        }
        binding.etEndTime.setOnClickListener {
            TimePicker()
        }
        binding.btnEventDetailContinue.alpha = 0.5f
        binding.btnEventDetailContinue.setOnClickListener {
            val isValid = validateAllFields()
            Log.e("VALIDATE", "validateAllFields() returned: $isValid")
            // Validate all fields before proceeding
            if (validateAllFields()) {
                // Send data to ViewModel
                eventCreationViewModel.eventName = binding.etEventName.text.toString().trim()
                eventCreationViewModel.memberLimit =
                    binding.etNoOfEventMem.text.toString().trim().toIntOrNull() ?: 0
                eventCreationViewModel.eventDate = binding.etSelectDate.text.toString().trim()
                eventCreationViewModel.eventStartTime = binding.etStartTime.text.toString().trim()
                eventCreationViewModel.eventEndTime = binding.etEndTime.text.toString().trim()
                Log.e(
                    "EventCreation",
                    "Group Details continue ButtonHandling: 118 eventName=${eventCreationViewModel.eventName}, " +
                            "memberLimit= ${eventCreationViewModel.memberLimit}, " +
                            "eventDate = ${eventCreationViewModel.eventDate}," +
                            "eventStartTime = ${eventCreationViewModel.eventStartTime}" +
                            "eventEndTime = ${eventCreationViewModel.eventEndTime}"
                )
                (activity as FragmentActivity).switchFragment(
                    R.id.EventCreationFragmentContainer,
                    EventDescFragment()
                )
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
            binding.etEventName,
            binding.etNoOfEventMem,
            binding.etSelectDate,
//            binding.etStartTime,
//            binding.etEndTime
        )
        for (field in fields) {
            field.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // When field gains focus, set to focused background
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    Log.d("FocusDebug", " field focus changed: $hasFocus")
                    if (view.id == R.id.etSelectDate) {
                        CustomDatePickerDialog.show(
                            context = requireContext(),
                            onDateSelected = { formattedDate, rawMillis ->
                                // Do whatever you want with the date
                                binding.etSelectDate.setText(formattedDate)
                            }
                        )
                    }
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
            R.id.etEventName -> {
                val eventName = binding.etEventName.text.toString().trim()
                if (eventName.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter the Event Name")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.etNoOfEventMem -> {
                val text = binding.etNoOfEventMem.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please Enter Event Members")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.etSelectDate -> {
                val selectDate = binding.etSelectDate.text.toString().trim()
                if (selectDate.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please Select Date")
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
        val EventName = binding.etEventName.text.toString().trim()
        if (EventName.isEmpty()) {
            binding.etEventName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        val noOfEventMem = binding.etNoOfEventMem.text.toString().trim()
        if (noOfEventMem.isEmpty()) {
            binding.etNoOfEventMem.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        val selectDateEvent = binding.etSelectDate.text.toString().trim()
        if (selectDateEvent.isEmpty()) {
            binding.etSelectDate.background =
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
        binding.etEventName.background = normalBackground
        binding.etNoOfEventMem.background = normalBackground
        binding.etSelectDate.background = normalBackground
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
        binding.etEventName.addTextChangedListener(textWatcher)
        binding.etNoOfEventMem.addTextChangedListener(textWatcher)
        binding.etSelectDate.addTextChangedListener(textWatcher)
        binding.etStartTime.addTextChangedListener(textWatcher)
        binding.etEndTime.addTextChangedListener(textWatcher)
    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("SignupBasicInfoFragment", "updateButtonState:$isValid")
        binding.btnEventDetailContinue.isEnabled = isValid
        binding.btnEventDetailContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val EventName = binding.etEventName.text.toString().trim()
        val noOfEventMem = binding.etNoOfEventMem.text.toString().trim()
        val selectDateEvent = binding.etSelectDate.text.toString().trim()
        val startTime = binding.etNoOfEventMem.text.toString().trim()
        val endTime = binding.etSelectDate.text.toString().trim()
        return EventName.isNotEmpty() &&
                noOfEventMem.isNotEmpty() &&
                selectDateEvent.isNotEmpty() &&
                startTime.isNotEmpty() &&
                endTime.isNotEmpty()
    }

    override fun onResume() {
        super.onResume()
        // Check if image is already selected and show it again
        setFieldFocusListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}