package com.example.recdeckapp.ui.fragments.createEvents

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentEventDescBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.ui.fragments.baseFragment.BaseMediaFragment
import com.example.recdeckapp.utils.FileDisplayUtils
import com.example.recdeckapp.utils.MediaPickerUtils
import com.example.recdeckapp.utils.TextFieldDescUtils
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.EventCreationViewModel

class EventDescFragment : BaseMediaFragment() {
    private var _binding: FragmentEventDescBinding? = null
    private val binding get() = _binding!!
    private var isValid = false
    private var selectedFile: Uri? = null
    private lateinit var eventCreationViewModel: EventCreationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        _binding = FragmentEventDescBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEventDescBinding.bind(view)
        eventCreationViewModel = (activity as EventCreationActivity).eventCreationViewModel
        setOnClickListener()
        setFieldFocusListeners()
        setupFieldListeners()
        updateButtonState()
        if (eventCreationViewModel.isEditing) {
            // Pre-populate fields with existing data
            binding.etEventDesc.setText(eventCreationViewModel.eventDescription)
            // Load image with FileDisplayUtils style
            eventCreationViewModel.eventImageUrl?.let { imageUrl ->
                val uri = Uri.parse(imageUrl)
                showSelectedFile(uri) // Use the same display method as new selections
                selectedFile = uri
            }
        }
        TextFieldDescUtils.setupDescWatcher(
            binding.etEventDesc,
            binding.tvDescCharCount,
            maxLength = 600
        ) { isValid ->
            //isDescValid = isValid
            //checkFormCompletion()
        }
        val activity = requireActivity() as? EventCreationActivity
        activity?.showStepIndicator(true)
        activity?.updateStepIndicator(3)
        activity?.updateTopBarForFragment(2)
    }

    private fun setOnClickListener() {
        binding.uploadLayoutCreateEvent.setOnClickListener {
            if (selectedFile != null) {
                Toast.makeText(
                    requireContext(),
                    "more than 1 file can not be upload",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            showMediaPickerBottomSheet(
                showCamera = true,
                showGallery = true,
                showFilePicker = false
            )
        }
        binding.btnEventDescContinue.alpha = 0.5f
        binding.btnEventDescContinue.setOnClickListener {
            if (validateAllFields()) {
                eventCreationViewModel.eventDescription = binding.etEventDesc.text.toString()
                eventCreationViewModel.eventImageUrl = selectedFile?.toString()
                Log.e(
                    "EventCreation",
                    "eventDescription continueButtonHandling: 85 rules=${eventCreationViewModel.eventDescription}," +
                            " eventImageUrl = ${eventCreationViewModel.eventImageUrl}"
                )
                (activity as FragmentActivity).switchFragment(
                    R.id.EventCreationFragmentContainer,
                    SelectPitchesEventFragment()
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
                // First remove any existing file views
                binding.fileContainer.removeAllViews()
                // Handle image selection for documents
                selectedFile = uri
                showSelectedFile(uri)
                //updateButtonState()
            },
            onFileSelected = { uri ->
//                // Handle file selection
//                selectedFile = uri
//                //signupDataViewModel.addDocumentPath(uri.toString())
//                showSelectedFile(uri)
//                //updateButtonState()
            }
        )
    }

    private fun showSelectedFile(uri: Uri) {
        val fileBinding = FileDisplayUtils.showSelectedFile(
            uri,
            requireContext(),
            binding.fileContainer
        ) {
            selectedFile = null
            if (binding.fileContainer.childCount == 0) {
                binding.uploadLayoutCreateEvent.visibility = View.VISIBLE
            }
        }
        // Now you can access the TextView
        fileBinding.tvchangeImage.visibility = View.VISIBLE
        fileBinding.tvchangeImage.setOnClickListener {
            // Handle change image click
            showMediaPickerBottomSheet(
                showCamera = true,
                showGallery = true,
                showFilePicker = false
            )
        }
    }

    private fun setFieldFocusListeners() {
        binding.etEventDesc.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
            } else {
                // When field loses focus, validate and set appropriate background
                validateFieldOnFocusLost(view)
            }
        }
    }

    private fun validateFieldOnFocusLost(view: View) {
        when (view.id) {
            R.id.etEventDesc -> {
                val text = binding.etEventDesc.text.toString().trim()
                if (text.isEmpty()) {
                    binding.etEventDesc.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter event description")
                } else {
                    binding.etEventDesc.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            else -> {
                binding.etEventDesc.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
            }
        }
    }

    private fun validateAllFields(): Boolean {
        var isAllValid = true
        // Reset all field backgrounds first
        resetFieldBackgrounds()
        // Validate group Name
        val eventDesc = binding.etEventDesc.text.toString().trim()
        if (eventDesc.isEmpty()) {
            binding.etEventDesc.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }
        return isAllValid
    }

    private fun resetFieldBackgrounds() {
        val normalBackground = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        binding.etEventDesc.background = normalBackground
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etEventDesc.addTextChangedListener(textWatcher)
    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("rulesFragment", "updateButtonState:$isValid")
        binding.btnEventDescContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val groupRules = binding.etEventDesc.text.toString().trim()
        return groupRules.isNotEmpty()
    }

    override fun onResume() {
        super.onResume()
        if (selectedFile != null) {
            binding.fileContainer.removeAllViews()
            selectedFile?.let { uri ->
                showSelectedFile(uri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}