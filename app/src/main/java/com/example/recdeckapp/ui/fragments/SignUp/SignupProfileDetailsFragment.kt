package com.example.recdeckapp.ui.fragments.SignUp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.data.userRole.UserRole
import com.example.recdeckapp.databinding.FragmentSignupProfileDetailsBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.ui.fragments.baseFragment.BaseMediaFragment
import com.example.recdeckapp.utils.BackPressHelper
import com.example.recdeckapp.utils.DropdownUtils
import com.example.recdeckapp.utils.MediaPickerUtils
import com.example.recdeckapp.utils.TextFieldDescUtils
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.SignupDataViewModel
import java.util.Calendar

class SignupProfileDetailsFragment : BaseMediaFragment() {
    // Declare binding object
    private var _binding: FragmentSignupProfileDetailsBinding? = null
    private val binding get() = _binding!!
    private var isDateSelected = false
    private var isGenderSelected = false
    private var isDatePickerShowing = false
    private var isDescValid = false
    private lateinit var signupDataViewModel: SignupDataViewModel
    private var imageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupProfileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupProfileDetailsBinding.bind(view)
        // Access the ViewModel from the Activity scope
        signupDataViewModel = (activity as SignupActivity).signupDataViewModel
        val role = signupDataViewModel.selectedRole
        when (role) {
            UserRole.ORGANIZER -> {
                showOrganizerFields()
                hideFacilityFields()
            }

            UserRole.FACILITY -> {
                showFacilityFields()
                hideOrganizerFields()
            }

            else -> {
            }
        }
        BackPressHelper.handleBackPress(this, binding.ivBackArrowSignUp2)
        binding.etDoBSignUp2.showSoftInputOnFocus = false
        checkFormCompletion()
        setFieldFocusListeners()
        setupGenderDropdown()
        setupDescCharCount()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        // Open Bottom Dialog on Camera Icon Click
        binding.cameraIconSignUp2.setOnClickListener {
            showMediaPickerBottomSheet(showFilePicker = false) // Only show camera and gallery
        }
        binding.etDoBSignUp2.setOnClickListener {
            if (!isDatePickerShowing) {
                isDatePickerShowing = true
                showDatePickerDialog()
                // Reset the flag after a small delay
                Handler(Looper.getMainLooper()).postDelayed({
                    isDatePickerShowing = false
                }, 1000) // 300 milliseconds delay
            }
        }
        binding.autoCompleteGender.setOnClickListener {
            // Manually show the dropdown when clicked
            binding.autoCompleteGender.showDropDown()
        }
        // Initially disable the continue button
        binding.btnSignUpForm2Continue.isEnabled = false
        binding.btnSignUpForm2Continue.alpha = 0.5f
        binding.btnSignUpForm2Continue.setOnClickListener {
            val role = signupDataViewModel.selectedRole
            when (role) {
                UserRole.ORGANIZER -> {
                    signupDataViewModel.dob = binding.etDoBSignUp2.text.toString()
                    signupDataViewModel.gender = binding.autoCompleteGender.text.toString()
                    signupDataViewModel.profilePicPath = imageUri.toString()
                    Log.e(
                        "SignupProfile",
                        "continueButtonHandling: 145 dob=${signupDataViewModel.dob}, gender= ${signupDataViewModel.gender}, imageUrl = ${signupDataViewModel.profilePicPath}"
                    )
                }

                UserRole.FACILITY -> {
                    signupDataViewModel.description = binding.etDescSignUp2.text.toString()
                    signupDataViewModel.profilePicPath = imageUri.toString()
                    Log.e(
                        "SignupProfile",
                        "continueButtonHandling: 145 desc = ${signupDataViewModel.description}, imageUrl = ${signupDataViewModel.profilePicPath}"
                    )
                }

                else -> {
                }
            }
            (activity as FragmentActivity).switchFragment(
                R.id.signupFragmentContainer,
                SignupInterestSelectionFragment()
            )
        }
    }

    private fun setupDescCharCount() {
        TextFieldDescUtils.setupDescWatcher(
            binding.etDescSignUp2,
            binding.tvDescCharCount,
            maxLength = 600
        ) { isValid ->
            isDescValid = isValid
            checkFormCompletion()
        }
    }

    private fun showOrganizerFields() {
        binding.clSignupOrganiserProfile.visibility = View.VISIBLE
    }

    private fun hideOrganizerFields() {
        binding.clSignupOrganiserProfile.visibility = View.GONE
    }

    private fun showFacilityFields() {
        binding.clSignupFacilityProfile.visibility = View.VISIBLE
    }

    private fun hideFacilityFields() {
        binding.clSignupFacilityProfile.visibility = View.GONE
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePickerDialog() {
        // Get current date as default
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->

                // Format date as dd/MM/yyyy or whatever format you want
                val formattedDate = String.format(
                    "%02d/%02d/%04d",
                    selectedDayOfMonth,
                    selectedMonth + 1,
                    selectedYear
                )

                binding.etDoBSignUp2.setText(formattedDate)
            },
            year,
            month,
            day
        )
        // Set max date to today to disable future dates
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun setupGenderDropdown() {
        // Gender options
        DropdownUtils.setupDropdown(
            context = requireContext(),
            anchorView = binding.autoCompleteGender,
            items = listOf("Male", "Female"),
            itemLayoutRes = R.layout.gender_dropdown,
            textViewId = R.id.genderNameTextView,
            dropdownBgColorRes = R.color.white_light
        ) { selected ->
            isGenderSelected = selected.isNotEmpty()
            checkFormCompletion()
            // Clear focus after selection
            binding.autoCompleteGender.clearFocus()
        }
        // Prevent keyboard from showing
        binding.autoCompleteGender.inputType = InputType.TYPE_NULL
        // Add focus and click listeners here
        binding.autoCompleteGender.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (view.id == R.id.autoCompleteGender) {
                    (view as? AutoCompleteTextView)?.showDropDown()
                }
                view.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
            } else {
                validateFieldOnFocusLost(view)
            }
        }
    }

    private fun setFieldFocusListeners() {
        val fields = listOf(
            binding.autoCompleteGender,
            binding.etDoBSignUp2,
            binding.etDescSignUp2
        )
        for (field in fields) {
            field.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // When field gains focus, set to focused background
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    Log.d("FocusDebug", "Gender field focus changed: $hasFocus")
                    if (view.id == R.id.etDoBSignUp2) {
                        showDatePickerDialog()
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
            R.id.etDoBSignUp2 -> {
                val password = binding.etDoBSignUp2.text.toString().trim()
                if (password.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please pick Date of Birth")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.autoCompleteGender -> {
                val text = binding.autoCompleteGender.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please select a gender")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.etDescSignUp2 -> {
                val password = binding.etDescSignUp2.text.toString().trim()
                if (password.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please Enter Description")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }
        }
    }

    // Method to check if all fields are completed
    private fun checkFormCompletion() {
        val role = signupDataViewModel.selectedRole
        when (role) {
            UserRole.ORGANIZER -> {
                if (isDateSelected && isGenderSelected) {
                    binding.btnSignUpForm2Continue.isEnabled = true
                    binding.btnSignUpForm2Continue.alpha = 1.0f
                } else {
                    binding.btnSignUpForm2Continue.isEnabled = false
                    binding.btnSignUpForm2Continue.alpha = 0.5f
                }
            }

            UserRole.FACILITY -> {
                if (isDescValid) {
                    binding.btnSignUpForm2Continue.isEnabled = true
                    binding.btnSignUpForm2Continue.alpha = 1.0f
                } else {
                    binding.btnSignUpForm2Continue.isEnabled = false
                    binding.btnSignUpForm2Continue.alpha = 0.5f
                }
            }

            else -> {
                binding.btnSignUpForm2Continue.isEnabled = false
                binding.btnSignUpForm2Continue.alpha = 0.5f
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
                binding.profilePictureSignUp2.setImageURI(uri)
                checkFormCompletion()
            },
            onFileSelected = { uri ->
                // Not needed in this fragment
            }
        )
    }

    override fun onResume() {
        super.onResume()
        // Check if image is already selected and show it again
        if (imageUri != null) {
            binding.profilePictureSignUp2.setImageURI(imageUri) // update with your imageView id
            checkFormCompletion()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to avoid memory leaks
        _binding = null
    }
}
