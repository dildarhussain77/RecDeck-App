package com.example.recdeckapp.ui.fragments.SignUp

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.BottomSheetDialogBinding
import com.example.recdeckapp.databinding.FragmentSignUpForm2Binding
import com.example.recdeckapp.ui.activities.SignupActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar

class SignUpForm2Fragment : Fragment(R.layout.fragment_sign_up_form2) {

    // Declare binding object
    private var _binding: FragmentSignUpForm2Binding? = null
    private val binding get() = _binding!!

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 101

    // Flags to check form completion
    private var isImageSelected = false
    private var isDateSelected = false
    private var isGenderSelected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpForm2Binding.bind(view)

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

        val genderOptions = listOf("Male", "Female", "Other")

// Ensure you are using the correct context (like `this@YourActivity`)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.autoCompleteGender.setAdapter(adapter)

// Show dropdown on click
        binding.autoCompleteGender.setOnClickListener {
            binding.autoCompleteGender.showDropDown()
        }
// Handle item selection
        binding.autoCompleteGender.setOnItemClickListener { parent, view, position, id ->
            val selectedGender = parent.getItemAtPosition(position).toString()
            isGenderSelected = true
            checkFormCompletion()
            Toast.makeText(requireContext(), "Selected: $selectedGender", Toast.LENGTH_SHORT).show()
        }

        // Date Picker for Date of Birth
        binding.etDoBSignUp2.setOnClickListener {
            val calendar = Calendar.getInstance()
            // Set a minimum age limit (e.g., 100 years back)
            val minCalendar = Calendar.getInstance().apply {
                add(Calendar.YEAR, -100) // 100 years back from today
            }

            // Create DatePickerDialog
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    binding.etDoBSignUp2.setText("$dayOfMonth/${month + 1}/$year")
                    isDateSelected = true
                    checkFormCompletion()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // 🌟 Set the maximum date to today to avoid future dates
            datePicker.datePicker.maxDate = calendar.timeInMillis

            // 🌟 Set the minimum date to 100 years back (optional)
            datePicker.datePicker.minDate = minCalendar.timeInMillis

            datePicker.show()
        }


        // Submit the form using binding
        binding.btnSignUpForm2Continue.setOnClickListener {
            (activity as SignupActivity).switchFragment(SignUpForm3Fragment())
            //requireActivity().finish()  // Close activity after submission
        }
    }

    // Method to check if all fields are completed
    private fun checkFormCompletion() {
        if (isImageSelected && isDateSelected && isGenderSelected) {
            binding.btnSignUpForm2Continue.isEnabled = true
            binding.btnSignUpForm2Continue.alpha = 1.0f
        } else {
            binding.btnSignUpForm2Continue.isEnabled = false
            binding.btnSignUpForm2Continue.alpha = 0.5f
        }
    }



    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = BottomSheetDialogBinding.inflate(LayoutInflater.from(context))

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
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to avoid memory leaks
        _binding = null
    }
}
