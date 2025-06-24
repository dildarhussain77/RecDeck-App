package com.example.recdeckapp.ui.fragments.SignUp

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
import com.example.recdeckapp.databinding.FragmentSignupDocumentUploadBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.ui.fragments.baseFragment.BaseMediaFragment
import com.example.recdeckapp.utils.BackPressHelper
import com.example.recdeckapp.utils.FileDisplayUtils
import com.example.recdeckapp.utils.MediaPickerUtils
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.SignupDataViewModel

class SignupDocumentUploadFragment : BaseMediaFragment() {
    private var _binding: FragmentSignupDocumentUploadBinding? = null
    private val binding get() = _binding!!
    private var etIdPassNum = false
    private lateinit var signupDataViewModel: SignupDataViewModel
    private val selectedFiles = mutableListOf<Uri>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupDocumentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signupDataViewModel = (activity as SignupActivity).signupDataViewModel
        binding.tvUploadDocsHeading.visibility = View.GONE
        BackPressHelper.handleBackPress(this, binding.ivBackArrowAttachDocs)
        setupFieldListeners()  // Set up text listeners to update the button state
        setOnClickListener()
        setFieldFocusListeners()
    }

    private fun setOnClickListener() {
        binding.uploadLayout.setOnClickListener {
            if (selectedFiles.size >= 2) {
                Toast.makeText(
                    requireContext(),
                    "more than 2 files can not be upload",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            showMediaPickerBottomSheet(
                showCamera = true,
                showGallery = false,
                showFilePicker = true
            )
        }
        // Initially disable the continue button
        binding.btnAttachDocsContinue.isEnabled = false
        binding.btnAttachDocsContinue.alpha = 0.5f
        binding.btnAttachDocsContinue.setOnClickListener {
            signupDataViewModel.idOrPassport = binding.etIdPassNum.text.toString()
            // Store the URI as string - it will be null if no file is selected
            Log.e(
                "SignupProfile",
                "Attach Docs continueButtonHandling: 88 idOrPassport=${signupDataViewModel.idOrPassport}," +
                        "docFilePath=${signupDataViewModel.docFilePaths}"
            )
            (activity as FragmentActivity).switchFragment(
                R.id.signupFragmentContainer,
                SignupSuccessFragment()
            )
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
                signupDataViewModel.addDocumentPath(uri.toString())
                showSelectedFile(uri)
                updateButtonState()
            },
            onFileSelected = { uri ->
                // Handle file selection
                selectedFiles.add(uri)
                signupDataViewModel.addDocumentPath(uri.toString())
                showSelectedFile(uri)
                updateButtonState()
            }
        )
    }

    private fun areAllFieldsFilled(): Boolean {
        val passNum = binding.etIdPassNum.text.toString().trim()
        // Check if field is filled AND file is selected
        return passNum.isNotEmpty() && selectedFiles.isNotEmpty()
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check fields and update button state
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etIdPassNum.addTextChangedListener(textWatcher)
    }

    private fun updateButtonState() {
        if (areAllFieldsFilled()) {
            binding.btnAttachDocsContinue.isEnabled = true
            binding.btnAttachDocsContinue.alpha = 1.0f
        } else {
            binding.btnAttachDocsContinue.isEnabled = false
            binding.btnAttachDocsContinue.alpha = 0.5f
        }
    }

    private fun showSelectedFile(uri: Uri) {
        FileDisplayUtils.showSelectedFile(
            uri,
            requireContext(),
            binding.fileListContainer
        ) { removedUri ->
            selectedFiles.remove(removedUri)
            signupDataViewModel.removeDocumentPath(removedUri.toString())
            updateButtonState()
            if (binding.fileListContainer.childCount == 0) {
                binding.tvUploadDocsHeading.visibility = View.GONE
            }
        }
        binding.tvUploadDocsHeading.visibility = View.VISIBLE
    }

    private fun setFieldFocusListeners() {
        binding.etIdPassNum.setOnFocusChangeListener { view, hasFocus ->
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
            R.id.etIdPassNum -> {
                val text = binding.etIdPassNum.text.toString().trim()
                if (text.isEmpty()) {
                    binding.etIdPassNum.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter a ID/Pass Number")
                } else {
                    binding.etIdPassNum.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            else -> {
                binding.etIdPassNum.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (selectedFiles.isNotEmpty()) {
            binding.fileListContainer.removeAllViews()
            selectedFiles.forEach { uri ->
                showSelectedFile(uri)
            }
            updateButtonState()  // update continue button state
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
