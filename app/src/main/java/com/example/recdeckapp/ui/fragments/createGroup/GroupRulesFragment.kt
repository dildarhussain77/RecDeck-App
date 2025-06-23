package com.example.recdeckapp.ui.fragments.createGroup

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
import com.example.recdeckapp.databinding.FragmentGroupRulesBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.EventCreationActivity
import com.example.recdeckapp.ui.activities.GroupCreationActivity
import com.example.recdeckapp.ui.fragments.baseFragment.BaseMediaFragment
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.FileDisplayUtils
import com.example.recdeckapp.utils.MediaPickerUtils
import com.example.recdeckapp.utils.TextFieldDescUtils
import com.example.recdeckapp.viewmodel.GroupCreationViewModel

class GroupRulesFragment : BaseMediaFragment() {

    private var _binding: FragmentGroupRulesBinding? = null
    private val binding get() = _binding!!
    private var selectedFile: Uri? = null
    private var isValid = false
    private lateinit var groupCreationViewModel: GroupCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentGroupRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGroupRulesBinding.bind(view)

        // Access the ViewModel from the Activity scope
        groupCreationViewModel = (activity as GroupCreationActivity).groupCreationViewModel

        setOnClickListener()
        setFieldFocusListeners()
        setupFieldListeners()
        updateButtonState()

        TextFieldDescUtils.setupDescWatcher(
            binding.etGroupRulesDesc,
            binding.tvDescCharCount,
            maxLength = 600
        ) { isValid ->
            //isDescValid = isValid
            //checkFormCompletion()
        }

        // Show the step indicator
        val activity = requireActivity() as? GroupCreationActivity
        activity?.showStepIndicator(true)
        // Step indicator pe current step set karo
        activity?.updateStepIndicator(3) // example: if it's step 2
        activity?.updateTopBarForFragment(3)

    }

    private fun setOnClickListener() {

        binding.uploadLayoutCreateGroup.setOnClickListener {
//            if (selectedFile.size >= 2) {
//                Toast.makeText(requireContext(), "more than 2 files can not be upload", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
            showMediaPickerBottomSheet(
                showCamera = true,
                showGallery = true,
                showFilePicker = false
            )
        }

        binding.btnGroupRulesContinue.alpha = 0.5f
        binding.btnGroupRulesContinue.setOnClickListener {

            if (validateAllFields()) {

                groupCreationViewModel.rules = binding.etGroupRulesDesc.text.toString()
                groupCreationViewModel.imageUrl = selectedFile?.toString()

                Log.d(
                    "GroupCreation",
                    "rules and image continueButtonHandling: 85 idddd=${groupCreationViewModel.creatorUserId}," +
                            " imageUrl = ${groupCreationViewModel.imageUrl}"
                )

                // Show loading
                binding.pgBarGroupCreated.visibility = View.VISIBLE
                binding.btnGroupRulesContinue.isEnabled = false

                groupCreationViewModel.insertGroupToRoom { success ->
                    requireActivity().runOnUiThread {
                        binding.pgBarGroupCreated.visibility = View.GONE

                        if (success) {
                            AlertDialogUtils.showCancelDialog(
                                requireContext(),
                                title = "Success!",
                                message = "Your group has been created successfully.",
                                showContinue = true,
                                customIconResId = R.drawable.ic_success,
                                onContinueClicked = {
                                    val intent =
                                        Intent(requireContext(), EventCreationActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                },
                            )
                        } else {
                            binding.btnGroupRulesContinue.isEnabled = true
                            (activity as? BaseActivity)?.showToast("Failed to create group")
                        }
                    }
                }
            } else {
                Log.e("VALIDATE", "setOnClickListener: line 93")
                (activity as? BaseActivity)?.showToast("Please Set Rules")
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
                selectedFile = uri
                //signupDataViewModel.addDocumentPath(uri.toString())
                showSelectedFile(uri)
                updateButtonState()
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
        FileDisplayUtils.showSelectedFile(
            uri,
            requireContext(),
            binding.fileContainer
        ) {
            selectedFile = null
            //signupDataViewModel.removeDocumentPath(removedUri.toString())
            //updateButtonState()
            if (binding.fileContainer.childCount == 0) {
                binding.uploadLayoutCreateGroup.visibility = View.VISIBLE
            }
        }

        binding.uploadLayoutCreateGroup.visibility = View.GONE
    }

    private fun setFieldFocusListeners() {
        binding.etGroupRulesDesc.setOnFocusChangeListener { view, hasFocus ->
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
            R.id.etGroupRulesDesc -> {
                val text = binding.etGroupRulesDesc.text.toString().trim()
                if (text.isEmpty()) {
                    binding.etGroupRulesDesc.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter group rules")
                } else {
                    binding.etGroupRulesDesc.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            else -> {
                binding.etGroupRulesDesc.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
            }
        }
    }

    private fun validateAllFields(): Boolean {
        var isAllValid = true
        // Reset all field backgrounds first
        resetFieldBackgrounds()

        // Validate group Name
        val groupName = binding.etGroupRulesDesc.text.toString().trim()
        if (groupName.isEmpty()) {
            binding.etGroupRulesDesc.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }


        return isAllValid
    }

    private fun resetFieldBackgrounds() {
        val normalBackground = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        binding.etGroupRulesDesc.background = normalBackground
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etGroupRulesDesc.addTextChangedListener(textWatcher)

    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("rulesFragment", "updateButtonState:$isValid")
        binding.btnGroupRulesContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val groupRules = binding.etGroupRulesDesc.text.toString().trim()

        return groupRules.isNotEmpty()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}