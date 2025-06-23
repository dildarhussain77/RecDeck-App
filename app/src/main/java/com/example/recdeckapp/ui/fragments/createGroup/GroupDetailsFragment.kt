package com.example.recdeckapp.ui.fragments.createGroup

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentGroupDetailsBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.GroupCreationActivity
import com.example.recdeckapp.utils.DropdownUtils
import com.example.recdeckapp.utils.TextFieldDescUtils
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.GroupCreationViewModel


class GroupDetailsFragment : Fragment() {

    private var _binding: FragmentGroupDetailsBinding? = null
    private val binding get() = _binding!!

    private var isPublicOrPrivateSelected = false
    private var isValid = false
    private lateinit var groupCreationViewModel: GroupCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentGroupDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGroupDetailsBinding.bind(view)

        // Access the ViewModel from the Activity scope
        groupCreationViewModel = (activity as GroupCreationActivity).groupCreationViewModel

        setOnClickListener()
        setupFieldFocusListeners()
        setupPublicOrPrivateDropdown()
        setupDescCharCount()
        setupFieldListeners()
        updateButtonState()

        // Show the step indicator
        val activity = requireActivity() as? GroupCreationActivity
        activity?.showStepIndicator(true)

        // Step indicator pe current step set karo
        activity?.updateStepIndicator(2) // example: if it's step 2
        activity?.updateTopBarForFragment(2)

    }


    private fun setOnClickListener() {

        binding.btnGroupDetailContinue.alpha = 0.5f
        binding.btnGroupDetailContinue.setOnClickListener {
            val isValid = validateAllFields()
            Log.e("VALIDATE", "validateAllFields() returned: $isValid")
            // Validate all fields before proceeding
            if (validateAllFields()) {
                Log.e("VALIDATE", "validateAllFields() returned: $isValid")

                // Send data to ViewModel
                groupCreationViewModel.groupName = binding.etGroupName.text.toString().trim()
                groupCreationViewModel.memberLimit =
                    binding.etNoOfGroupMem.text.toString().trim().toIntOrNull() ?: 0
                groupCreationViewModel.description =
                    binding.etCreateGroupDesc.text.toString().trim()
                groupCreationViewModel.accessType =
                    binding.autoCompletePublicOrPrivate.text.toString().trim()

                Log.e(
                    "GroupCreation",
                    "Group Details continue ButtonHandling: 84 groupName=${groupCreationViewModel.groupName}, " +
                            "memberLimit= ${groupCreationViewModel.memberLimit}, " +
                            "description = ${groupCreationViewModel.description}," +
                            "accessType = ${groupCreationViewModel.accessType}"
                )
                Log.e(
                    "GroupCreation",
                    "setOnClickListener: ${groupCreationViewModel.creatorUserId}",
                )

                (activity as FragmentActivity).switchFragment(
                    R.id.GroupCreationFragmentContainer,
                    GroupRulesFragment()
                )

            } else {
                Log.e("VALIDATE", "setOnClickListener: line 93")
                (activity as? BaseActivity)?.showToast("Please fill all fields")
            }

        }
    }

    private fun setupPublicOrPrivateDropdown() {
        DropdownUtils.setupDropdown(
            context = requireContext(),
            anchorView = binding.autoCompletePublicOrPrivate,
            items = listOf("Public", "Private"),
            itemLayoutRes = R.layout.public_private_dropdown,
            textViewId = R.id.tvPublicorPrivate,
            dropdownBgColorRes = R.color.white_light
        ) { selected ->
            isPublicOrPrivateSelected = true
            //(activity as? BaseActivity)?.showToast("Selected: $selected")
        }

        // Prevent keyboard from showing
        binding.autoCompletePublicOrPrivate.inputType = InputType.TYPE_NULL

        // Add focus and click listeners here
        binding.autoCompletePublicOrPrivate.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (view.id == R.id.autoCompletePublicOrPrivate) {
                    (view as? AutoCompleteTextView)?.showDropDown()
                }

                view.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
            } else {
                validateFieldOnFocusLost(view)
            }
        }
    }

    private fun setupFieldFocusListeners() {
        val fields = listOf(
            binding.etGroupName,
            binding.etNoOfGroupMem,
            binding.autoCompletePublicOrPrivate,
            binding.etCreateGroupDesc,
        )

        for (field in fields) {
            field.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // When field gains focus, set to focused background
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                } else {
                    // When field loses focus, validate and set appropriate background
                    validateFieldOnFocusLost(view)

                }
            }
        }
    }

    private fun validateFieldOnFocusLost(view: View) {
        when (view.id) {
            R.id.etGroupName -> {
                val text = binding.etGroupName.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter a Group Name")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.etNoOfGroupMem -> {
                val email = binding.etNoOfGroupMem.text.toString().trim()
                if (email.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter Group Members")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.autoCompletePublicOrPrivate -> {
                val password = binding.autoCompletePublicOrPrivate.text.toString().trim()
                if (password.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please select type")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.etCreateGroupDesc -> {
                val text = binding.etCreateGroupDesc.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter Description")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            else -> {
                view.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
            }
        }
    }

    private fun validateAllFields(): Boolean {
        var isAllValid = true
        // Reset all field backgrounds first
        resetFieldBackgrounds()

        // Validate group Name
        val groupName = binding.etGroupName.text.toString().trim()
        if (groupName.isEmpty()) {
            binding.etGroupName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }

        val noOfGroupMem = binding.etGroupName.text.toString().trim()
        if (noOfGroupMem.isEmpty()) {
            binding.etGroupName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }

        val autoCompletePublicOrPrivate = binding.etGroupName.text.toString().trim()
        if (autoCompletePublicOrPrivate.isEmpty()) {
            binding.etGroupName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }

        val createGroupDesc = binding.etGroupName.text.toString().trim()
        if (createGroupDesc.isEmpty()) {
            binding.etGroupName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        }

        return isAllValid
    }

    private fun resetFieldBackgrounds() {
        val normalBackground = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        binding.etGroupName.background = normalBackground
        binding.etNoOfGroupMem.background = normalBackground
        binding.autoCompletePublicOrPrivate.background = normalBackground
        binding.etCreateGroupDesc.background = normalBackground
    }


    private fun setupDescCharCount() {
        TextFieldDescUtils.setupDescWatcher(
            binding.etCreateGroupDesc,
            binding.tvDescCharCount,
            maxLength = 600
        ) { isValid ->
            //isDescValid = isValid
            //checkFormCompletion()
        }
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etGroupName.addTextChangedListener(textWatcher)
        binding.etNoOfGroupMem.addTextChangedListener(textWatcher)
        binding.autoCompletePublicOrPrivate.addTextChangedListener(textWatcher)
        binding.etCreateGroupDesc.addTextChangedListener(textWatcher)

    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("SignupBasicInfoFragment", "updateButtonState:$isValid")
        binding.btnGroupDetailContinue.isEnabled = isValid
        binding.btnGroupDetailContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val groupName = binding.etGroupName.text.toString().trim()
        val noOfGroupMem = binding.etNoOfGroupMem.text.toString().trim()
        val autoCompletePublicOrPrivate = binding.autoCompletePublicOrPrivate.text.toString().trim()
        val createGroupDesc = binding.etCreateGroupDesc.text.toString().trim()

        return groupName.isNotEmpty() &&
                noOfGroupMem.isNotEmpty() &&
                autoCompletePublicOrPrivate.isNotEmpty() &&
                createGroupDesc.isNotEmpty()
    }

    override fun onResume() {
        super.onResume()
        // Check if image is already selected and show it again
        setupFieldFocusListeners()
        setupPublicOrPrivateDropdown()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}