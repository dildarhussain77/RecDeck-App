package com.example.recdeckapp.ui.fragments.SignUp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recdeckapp.R
import com.example.recdeckapp.data.UserRole
import com.example.recdeckapp.databinding.BottomSheetFileUploadBinding
import com.example.recdeckapp.databinding.DialogPermissionBinding
import com.example.recdeckapp.databinding.FragmentSignupProfileDetailsBinding
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.viewmodel.SignupViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignupProfileDetailsFragment : Fragment(R.layout.fragment_signup_profile_details) {

    // Declare binding object
    private var _binding: FragmentSignupProfileDetailsBinding? = null
    private val binding get() = _binding!!

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 101

    // Flags to check form completion
    private var isImageSelected = false
    private var isDateSelected = false
    private var isGenderSelected = false

    private var isDatePickerShowing = false

    private var isDescValid = false

    private var selectedRole: UserRole? = null
    private lateinit var signupViewModel: SignupViewModel



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
        signupViewModel = (activity as SignupActivity).signupViewModel

        signupViewModel.selectedRole.observe(viewLifecycleOwner) { role ->
            selectedRole = role
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
                    // Optional: Hide both if no role selected
                }
            }
            checkFormCompletion() // <-- Trigger validation after role change
        }

        binding.etDescSignUp2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isDescValid = !s.isNullOrBlank()
                checkFormCompletion()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Initially disable the continue button
        binding.btnSignUpForm2Continue.isEnabled = false
        binding.btnSignUpForm2Continue.alpha = 0.5f


        binding.ivBackArrowSignUp2.bringToFront()
        binding.ivBackArrowSignUp2.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Open Bottom Dialog on Camera Icon Click
        binding.cameraIconSignUp2.setOnClickListener {
            showBottomSheetDialog()
        }


        setFieldFocusListeners()
        setupGenderDropdown()
        setupDescCharacterCounter()

        binding.etDoBSignUp2.setOnClickListener {
            if (!isDatePickerShowing) {
                isDatePickerShowing = true
                showDatePicker()

                // Reset the flag after a small delay
                Handler(Looper.getMainLooper()).postDelayed({
                    isDatePickerShowing = false
                }, 1000) // 300 milliseconds delay
            }
        }

        // Submit the form using binding
        binding.btnSignUpForm2Continue.setOnClickListener {
            (activity as SignupActivity).switchFragment(SignupInterestSelectionFragment())
            //requireActivity().finish()  // Close activity after submission
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

    private fun showDatePicker() {

        // Calendar Constraints to block future dates
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())  // Only past and current dates allowed

        // Create Material Date Picker with constraints
        val datePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText("Select Date of Birth")
                .setTheme(R.style.CustomDatePicker)  // Apply custom theme
                .setCalendarConstraints(constraintsBuilder.build())  // Set constraints
                .build()

        // Date selection callback
        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = selection
            }

            // Format date as "dd/MM/yyyy"
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)

            // Display selected date in EditText
            binding.etDoBSignUp2.setText(selectedDate)

            isDateSelected = true
            checkFormCompletion()
        }

        // Show the date picker dialog
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun setupGenderDropdown() {
        // Gender options
        val genderOptions = listOf("Male", "Female")

        // Custom ArrayAdapter for the gender dropdown
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.gender_dropdown,  // Your custom layout for gender items
            R.id.genderNameTextView,   // TextView ID from the custom layout
            genderOptions
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.gender_dropdown, parent, false)

                // Get the gender from the adapter
                val gender = getItem(position) ?: return view
                // Set the gender name to the TextView in the custom layout
                view.findViewById<TextView>(R.id.genderNameTextView).text = gender

                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return getView(position, convertView, parent)
            }
        }

        // Set up the gender AutoCompleteTextView with the custom adapter
        binding.autoCompleteGender.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(R.color.white_light)  // Custom dropdown background

            // Show dropdown when focused or clicked
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showDropDown()
            }
            // Show all options when clicked
            setOnClickListener {
                (adapter as ArrayAdapter<*>).filter.filter(null) // Clear any filter
                showDropDown()
            }
            // Handle item selection
            setOnItemClickListener { parent, view, position, id ->
                val selectedGender = parent.getItemAtPosition(position).toString()
                isGenderSelected = true
                checkFormCompletion()
                Toast.makeText(requireContext(), "Selected: $selectedGender", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun setFieldFocusListeners() {
        val fields = listOf(binding.autoCompleteGender, binding.etDoBSignUp2)
        for (field in fields) {
            field.setOnFocusChangeListener { _, hasFocus ->
                val text = field.text.toString().trim()
                val background =
                    if (hasFocus || text.isNotEmpty()) R.drawable.bg_edit_text_focused else R.drawable.bg_edit_text
                field.background = ContextCompat.getDrawable(requireContext(), background)
            }
        }
    }

    // Method to check if all fields are completed
    private fun checkFormCompletion() {
        when (selectedRole) {
            UserRole.ORGANIZER -> {
                if (isImageSelected && isDateSelected && isGenderSelected) {
                    binding.btnSignUpForm2Continue.isEnabled = true
                    binding.btnSignUpForm2Continue.alpha = 1.0f
                } else {
                    binding.btnSignUpForm2Continue.isEnabled = false
                    binding.btnSignUpForm2Continue.alpha = 0.5f
                }
            }
            UserRole.FACILITY -> {
                if (isImageSelected && isDescValid) {
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


    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = BottomSheetFileUploadBinding.inflate(LayoutInflater.from(context))

        // Close the BottomSheet when the close icon is clicked
        bottomSheetBinding.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.imgCameraBottomDialog.setOnClickListener {
            checkCameraPermission()
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.imgGalleryBottomDialog.setOnClickListener {
            openGallery()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            // Show a dialog explaining why the permission is needed
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE
            )
        }
    }

    private fun showPermissionRationaleDialog() {
        val dialogBinding = DialogPermissionBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvMessage.text = "Camera permission is required. Please enable it from settings."
        dialogBinding.tvGoToSetting.text = "Go to Settings"
        dialogBinding.tvCancel.text = "Cancel"

        dialogBinding.tvGoToSetting.setOnClickListener {
            // Open app settings to enable permission manually
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
            alertDialog.dismiss()
        }

        dialogBinding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

//    private fun checkGalleryPermission() {
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            openGallery()
//        }
//        else {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                GALLERY_REQUEST_CODE
//            )
//        }
//    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as? android.graphics.Bitmap
                    binding.profilePictureSignUp2.setImageBitmap(imageBitmap)
                    isImageSelected = true
                    checkFormCompletion()
                    Toast.makeText(requireContext(), "Image Captured!", Toast.LENGTH_SHORT).show()
                }


                GALLERY_REQUEST_CODE -> {
                    val imageUri: Uri? = data?.data
                    binding.profilePictureSignUp2.setImageURI(imageUri)
                    isImageSelected = true
                    checkFormCompletion()
                    Toast.makeText(requireContext(), "Image Selected!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
//        if (requestCode == GALLERY_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                openGallery()
//            } else {
//                Toast.makeText(requireContext(), "Camera Permission Denied", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
    }

    // Add this new method to your fragment
    private fun setupDescCharacterCounter() {
        binding.etDescSignUp2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                binding.tvDescCharCount.text = "$currentLength/600"

                // Optional: Update validation state if you need bio to be validated
                isDescValid = currentLength in 1..600 // Or whatever your validation rules are
                checkFormCompletion()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        // Refresh or update any necessary data when fragment resumes
        checkFormCompletion()

        // Optionally, update any UI elements that may have changed
        if (isImageSelected) {
            binding.profilePictureSignUp2.visibility = View.VISIBLE
        }
        if (isDateSelected) {
            binding.etDoBSignUp2.alpha = 1.0f
        }
        if (isGenderSelected) {
            binding.autoCompleteGender.alpha = 1.0f
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to avoid memory leaks
        _binding = null
    }
}
