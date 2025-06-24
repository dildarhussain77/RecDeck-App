package com.example.recdeckapp.ui.fragments.createEvents

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentEventPaymentBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.DropdownUtils
import com.example.recdeckapp.viewmodel.EventCreationViewModel

class EventPaymentFragment : Fragment() {
    private var _binding: FragmentEventPaymentBinding? = null
    private val binding get() = _binding!!
    private var isEventRepeatSelected = false
    private var isPaymentTypeSelected = false
    private var isValid = false
    private lateinit var eventCreationViewModel: EventCreationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        _binding = FragmentEventPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEventPaymentBinding.bind(view)
        eventCreationViewModel = (activity as EventCreationActivity).eventCreationViewModel
        val activity = requireActivity() as? EventCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(6)
        activity?.updateTopBarForFragment(5)
        setOnClickListener()
        setupEventRepeatDropdown()
        setupPaymentTypeDropdown()
        setupFieldListeners()
        updateButtonState()
        if (eventCreationViewModel.isEditing) {
            // Pre-populate fields with existing data
            binding.autoCompleteEventRepeat.setText(eventCreationViewModel.eventRepeat)
            binding.autoCompletePaymentType.setText(eventCreationViewModel.eventPaymentType)
            // Trigger validation
            isEventRepeatSelected = true
            isPaymentTypeSelected = true
            updateButtonState()
        }
    }

    private fun setOnClickListener() {
        binding.btnEventPaymentContinue.alpha = 0.5f
        binding.btnEventPaymentContinue.setOnClickListener {
            if (validateAllFields()) {
                eventCreationViewModel.eventRepeat = binding.autoCompleteEventRepeat.text.toString()
                eventCreationViewModel.eventPaymentType =
                    binding.autoCompletePaymentType.text.toString()
                Log.e(
                    "EventCreation",
                    "eventRepeat continue ButtonHandling: 48 eventRepeat=${eventCreationViewModel.eventRepeat}," +
                            "eventPaymentType = ${eventCreationViewModel.eventPaymentType}"
                )
                // Show loading
                binding.pgBarEventCreated.visibility = View.VISIBLE
                binding.btnEventPaymentContinue.isEnabled = false
                eventCreationViewModel.saveOrUpdateEvent { success ->
                    requireActivity().runOnUiThread {
                        binding.pgBarEventCreated.visibility = View.GONE
                        if (success) {
                            AlertDialogUtils.showCancelDialog(
                                requireContext(),
                                title = "Success!",
                                message = "Your Event has been created successfully.",
                                showContinue = true,
                                customIconResId = R.drawable.ic_success,
                                onContinueClicked = {
                                    requireActivity().finish()
                                },
                            )
                        } else {
                            binding.btnEventPaymentContinue.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                "Failed to create event",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupEventRepeatDropdown() {
        val items = listOf("Repeat", "Do not repeat")
        DropdownUtils.setupDropdown(
            context = requireContext(),
            anchorView = binding.autoCompleteEventRepeat,
            items = items,
            itemLayoutRes = R.layout.common_dropdown,
            textViewId = R.id.tvCommonDropdown,
            dropdownBgColorRes = R.color.white_light
        ) { selected ->
            isEventRepeatSelected = true
            eventCreationViewModel.eventRepeat = selected
            updateButtonState()
        }
        // Prevent keyboard from showing
        binding.autoCompleteEventRepeat.inputType = InputType.TYPE_NULL
        // Add focus and click listeners here
        binding.autoCompleteEventRepeat.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (view.id == R.id.autoCompleteEventRepeat) {
                    (view as? AutoCompleteTextView)?.showDropDown()
                }
                view.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
            } else {
                validateFieldOnFocusLost(view)
            }
        }
    }

    private fun setupPaymentTypeDropdown() {
        val items = listOf("Self", "Split in members")
        DropdownUtils.setupDropdown(
            context = requireContext(),
            anchorView = binding.autoCompletePaymentType,
            items = items,
            itemLayoutRes = R.layout.common_dropdown,
            textViewId = R.id.tvCommonDropdown,
            dropdownBgColorRes = R.color.white_light
        ) { selected ->
            isPaymentTypeSelected = true
            eventCreationViewModel.eventPaymentType = selected
            updateButtonState()
            //  Show/hide RadioGroup based on selection
            if (selected == "Split in members") {
                binding.radioPaymentsForOther.visibility = View.GONE
                binding.tvPaymentsForOther.visibility = View.GONE
            } else {
                binding.radioPaymentsForOther.visibility = View.VISIBLE
                binding.tvPaymentsForOther.visibility = View.VISIBLE
            }
        }
        // Prevent keyboard from showing
        binding.autoCompletePaymentType.inputType = InputType.TYPE_NULL
        // Add focus and click listeners here
        binding.autoCompletePaymentType.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (view.id == R.id.autoCompletePaymentType) {
                    (view as? AutoCompleteTextView)?.showDropDown()
                }
                view.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
            } else {
                validateFieldOnFocusLost(view)
            }
        }
    }

    private fun validateFieldOnFocusLost(view: View) {
        when (view.id) {
            R.id.autoCompleteEventRepeat -> {
                val text = binding.autoCompleteEventRepeat.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please select Event Status")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.autoCompletePaymentType -> {
                val text = binding.autoCompletePaymentType.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please select payment Type")
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
        val eventRepeat = binding.autoCompleteEventRepeat.text.toString().trim()
        if (eventRepeat.isEmpty()) {
            binding.autoCompleteEventRepeat.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        val paymentType = binding.autoCompletePaymentType.text.toString().trim()
        if (paymentType.isEmpty()) {
            binding.autoCompletePaymentType.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        return isAllValid
    }

    private fun resetFieldBackgrounds() {
        val normalBackground = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        binding.autoCompleteEventRepeat.background = normalBackground
        binding.autoCompletePaymentType.background = normalBackground
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.autoCompleteEventRepeat.addTextChangedListener(textWatcher)
        binding.autoCompletePaymentType.addTextChangedListener(textWatcher)
    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("SignupBasicInfoFragment", "updateButtonState:$isValid")
        binding.btnEventPaymentContinue.isEnabled = isValid
        binding.btnEventPaymentContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val eventRepeat = binding.autoCompleteEventRepeat.text.toString().trim()
        val paymentType = binding.autoCompletePaymentType.text.toString().trim()
        return eventRepeat.isNotEmpty() &&
                paymentType.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}