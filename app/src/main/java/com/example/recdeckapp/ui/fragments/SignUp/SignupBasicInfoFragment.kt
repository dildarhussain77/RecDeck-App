package com.example.recdeckapp.ui.fragments.SignUp

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.recdeckapp.R
import com.example.recdeckapp.data.entities.City
import com.example.recdeckapp.data.entities.Country
import com.example.recdeckapp.databinding.FragmentSignupBasicInfoBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.utils.BackPressHelper
import com.example.recdeckapp.utils.PasswordToggleHelper
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.SignupDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SignupBasicInfoFragment : Fragment(R.layout.fragment_signup_basic_info) {
    private var _binding: FragmentSignupBasicInfoBinding? = null
    private val binding get() = _binding!!
    private var isTickSelected = false
    private var isValid = false
    private lateinit var passwordToggleHelper: PasswordToggleHelper
    private lateinit var confirmPasswordToggleHelper: PasswordToggleHelper
    private lateinit var signupDataViewModel: SignupDataViewModel
    private var emailCheckJob: Job? = null

    // Sample data with flags
    private fun countries() = listOf(
        Country("UAE", R.drawable.uae, listOf("Abu Dhabi", "Dubai", "Sharjah", "Ajman")),
        Country("USA", R.drawable.usa, listOf("New York", "Los Angeles", "Chicago")),
        Country("Pak", R.drawable.pakistan, listOf("Islamabad", "Lahore", "Karachi")),
        Country("India", R.drawable.india, listOf("Mumbai", "Delhi", "Bangalore"))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBasicInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signupDataViewModel = (activity as SignupActivity).signupDataViewModel
        Log.e("SignUp", "selected role:${signupDataViewModel.selectedRole}")
        BackPressHelper.handleBackPress(this, binding.ivBackArrowSignUp1)
        setupCountryDropdown()
        setupCityDropdown()
        setupFieldFocusListeners()
        setupFieldListeners()
        updateButtonState()
        isTickSelect()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        //Toggle Passwords
        passwordToggleHelper =
            PasswordToggleHelper(binding.etSignUp1Password, binding.ivTogglePasswordSignUp1)
        confirmPasswordToggleHelper = PasswordToggleHelper(
            binding.etSignUp1ConfirmPassword,
            binding.ivToggleConfirmPasswordSignUp
        )
        binding.ivTogglePasswordSignUp1.setOnClickListener {
            passwordToggleHelper.toggle()
        }
        binding.ivToggleConfirmPasswordSignUp.setOnClickListener {
            confirmPasswordToggleHelper.toggle()
        }
        // Initially disable the continue button
        //binding.btnSignUpForm1Continue.isEnabled = false
        binding.btnSignUpForm1Continue.alpha = 0.5f
        binding.btnSignUpForm1Continue.setOnClickListener {
            // Validate all fields before proceeding
            if (validateAllFields()) {
                // Send data to ViewModel
                signupDataViewModel.fullName = binding.etFullNameSignUp1.text.toString().trim()
                signupDataViewModel.email = binding.etSignUp1Email.text.toString().trim()
                signupDataViewModel.password = binding.etSignUp1Password.text.toString().trim()
                signupDataViewModel.country = binding.countryAutoComplete.text.toString().trim()
                signupDataViewModel.city = binding.cityAutoComplete.text.toString().trim()
                Log.e(
                    "SignupProfile",
                    "Basic Info continueButtonHandling: 99 fullName=${signupDataViewModel.fullName}, " +
                            "email= ${signupDataViewModel.email}, " +
                            "password = ${signupDataViewModel.password}," +
                            "country = ${signupDataViewModel.country}," +
                            "city = ${signupDataViewModel.city}"
                )
                (activity as FragmentActivity).switchFragment(
                    R.id.signupFragmentContainer,
                    SignupProfileDetailsFragment()
                )
            } else {
                //(activity as? BaseActivity)?.showToast("Please fill are Req")
            }
        }
    }

    private fun validateAllFields(): Boolean {
        var isAllValid = true
        // Reset all field backgrounds first
        resetFieldBackgrounds()
        // Validate Full Name
        val fullName = binding.etFullNameSignUp1.text.toString().trim()
        if (fullName.isEmpty()) {
            binding.etFullNameSignUp1.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        } else {
            binding.etFullNameSignUp1.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        }
        // Validate Email
        val email = binding.etSignUp1Email.text.toString().trim()
        if (email.isEmpty()) {
            binding.etSignUp1Email.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        } else if (!isValidEmail(email)) {
            binding.etSignUp1Email.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            (activity as? BaseActivity)?.showToast("Please enter a valid email address")
            isAllValid = false
        } else if (!runBlocking { signupDataViewModel.isEmailAvailable(email) }) {
            binding.etSignUp1Email.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            (activity as? BaseActivity)?.showToast("Email already taken")
            isAllValid = false
        } else if (email.isNotEmpty()) {
            binding.etSignUp1Email.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        }
        // 4. Update the email field listeners
        binding.etSignUp1Email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s?.toString()?.trim() ?: ""
                checkEmailAvailability(email)
                updateButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.etSignUp1Email.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = binding.etSignUp1Email.text.toString().trim()
                if (email.isNotEmpty() && isValidEmail(email)) {
                    lifecycleScope.launch {
                        if (!signupDataViewModel.isEmailAvailable(email)) {
                            binding.etSignUp1Email.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.bg_edit_text_error
                            )
                            (activity as? BaseActivity)?.showToast("Email already taken")
                        }
                    }
                }
            }
        }
        // Validate Password
        val password = binding.etSignUp1Password.text.toString().trim()
        if (password.isEmpty()) {
            binding.etSignUp1Password.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        } else if (password.length < 6) {
            binding.etSignUp1Password.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            (activity as? BaseActivity)?.showToast("Password must be at least 6 characters long")
            isAllValid = false
        } else {
            binding.etSignUp1Password.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        }
        // Validate Confirm Password
        val confirmPassword = binding.etSignUp1ConfirmPassword.text.toString().trim()
        if (confirmPassword.isEmpty()) {
            binding.etSignUp1ConfirmPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        } else if (password != confirmPassword) {
            binding.etSignUp1Password.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            binding.etSignUp1ConfirmPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            (activity as? BaseActivity)?.showToast("Passwords do not match")
            isAllValid = false
        } else {
            binding.etSignUp1ConfirmPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        }
        // Validate Country
        val country = binding.countryAutoComplete.text.toString().trim()
        if (country.isEmpty()) {
            binding.countryAutoComplete.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        } else {
            binding.countryAutoComplete.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        }
        // Validate City
        val city = binding.cityAutoComplete.text.toString().trim()
        if (city.isEmpty()) {
            binding.cityAutoComplete.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            isAllValid = false
        } else {
            binding.cityAutoComplete.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        }
        // Validate Terms & Conditions
        if (!isTickSelected) {
            isAllValid = false
        }
        // Show general error message if any field is invalid
        if (!isAllValid) {
            (activity as? BaseActivity)?.showToast("Please fill all required fields correctly")
        }
        return isAllValid
    }

    private fun resetFieldBackgrounds() {
        val normalBackground = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        binding.etFullNameSignUp1.background = normalBackground
        binding.etSignUp1Email.background = normalBackground
        binding.etSignUp1Password.background = normalBackground
        binding.etSignUp1ConfirmPassword.background = normalBackground
        binding.countryAutoComplete.background = normalBackground
        binding.cityAutoComplete.background = normalBackground
    }

    //function for real-time email checking
    private fun checkEmailAvailability(email: String) {
        emailCheckJob?.cancel()
        emailCheckJob = lifecycleScope.launch {
            delay(1000) // Wait for user to stop typing
            if (email.isNotEmpty() && isValidEmail(email)) {
                if (!signupDataViewModel.isEmailAvailable(email)) {
                    binding.etSignUp1Email.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Email already taken")
                }
            }
        }
    }

    private fun setupCountryDropdown() {
        val adapter = object : ArrayAdapter<Country>(
            requireContext(), R.layout.country_dropdown_item, R.id.countryNameTextView, countries()
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.country_dropdown_item, parent, false)
                val country = getItem(position) ?: return view
                view.findViewById<ImageView>(R.id.flagImageView).setImageResource(country.flagResId)
                view.findViewById<TextView>(R.id.countryNameTextView).text = country.name
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return getView(position, convertView, parent)
            }
        }
        binding.countryAutoComplete.setAdapter(adapter)
        binding.countryAutoComplete.setOnClickListener {
            binding.countryAutoComplete.showDropDown()
        }
        binding.countryAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = adapter.getItem(position) ?: return@setOnItemClickListener
            val circularFlag = getCircularDrawable(selectedCountry.flagResId, 60, 14)
            binding.countryAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
                circularFlag,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down),
                null
            )
            binding.countryAutoComplete.setText(selectedCountry.name, false)
            updateCitiesDropdown(selectedCountry.cities)
            // Reset country field background when valid selection is made
            binding.countryAutoComplete.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
        }
    }

    private fun getCircularDrawable(drawableRes: Int, size: Int, margin: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableRes) as? BitmapDrawable
            ?: return null
        val bitmap = drawable.bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        val circularDrawable = BitmapDrawable(resources, output)
        val insetDrawable = InsetDrawable(circularDrawable, margin)
        return insetDrawable
    }

    private fun setupCityDropdown() {
        val adapter = object : ArrayAdapter<City>(
            requireContext(), R.layout.city_dropdown,
            R.id.cityNameTextView,
            emptyList()
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.city_dropdown, parent, false)
                val city = getItem(position) ?: return view
                view.findViewById<TextView>(R.id.cityNameTextView).text = city.name
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return getView(position, convertView, parent)
            }
        }
        binding.cityAutoComplete.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(R.color.white_light)
            setOnClickListener {
                showDropDown()
            }
            setOnItemClickListener { _, _, _, _ ->
                // Reset city field background when valid selection is made
                binding.cityAutoComplete.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
            }
        }
    }

    private fun updateCitiesDropdown(cities: List<String>) {
        val cityObjects = cities.map { City(it) }
        val adapter = object : ArrayAdapter<City>(
            requireContext(), R.layout.city_dropdown,
            R.id.cityNameTextView,
            cityObjects
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.city_dropdown, parent, false)
                val city = getItem(position) ?: return view
                view.findViewById<TextView>(R.id.cityNameTextView).text = city.name
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return getView(position, convertView, parent)
            }
        }
        binding.cityAutoComplete.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        binding.cityAutoComplete.setText("", false)
    }

    private fun isTickSelect() {
        binding.llTick.setOnClickListener {
            isTickSelected = !isTickSelected
            if (isTickSelected) {
                binding.icTick.setImageResource(R.drawable.ic_tick_selected)
            } else {
                binding.icTick.setImageResource(R.drawable.ic_tick_unselected)
            }
            updateButtonState()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setupFieldFocusListeners() {
        val fields = listOf(
            binding.etFullNameSignUp1,
            binding.etSignUp1Email,
            binding.etSignUp1Password,
            binding.etSignUp1ConfirmPassword,
            binding.countryAutoComplete,
            binding.cityAutoComplete
        )
        for (field in fields) {
            field.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // When field gains focus, set to focused background
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    if (view.id == R.id.cityAutoComplete) {
                        (view as? AutoCompleteTextView)?.showDropDown()
                    }
                    if (view.id == R.id.countryAutoComplete) {
                        (view as? AutoCompleteTextView)?.showDropDown()
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
            R.id.etFullNameSignUp1 -> {
                val text = binding.etFullNameSignUp1.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter a Full Name")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.etSignUp1Email -> {
                val email = binding.etSignUp1Email.text.toString().trim()
                if (email.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter a email address")
                } else if (!isValidEmail(email)) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter a valid email address")
                } else {
                    // Check email availability in database
                    lifecycleScope.launch {
                        val isAvailable = signupDataViewModel.isEmailAvailable(email)
                        withContext(Dispatchers.Main) {
                            if (!isAvailable) {
                                view.background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.bg_edit_text_error
                                )
                                (activity as? BaseActivity)?.showToast("Email already taken")
                            } else {
                                view.background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.bg_edit_text_focused
                                )
                            }
                        }
                    }
                }
            }

            R.id.etSignUp1Password -> {
                val password = binding.etSignUp1Password.text.toString().trim()
                if (password.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please enter Password")
                } else if (password.length < 6) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Password must be at least 6 characters long")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                    // Also validate confirm password if it has text
                    val confirmPassword = binding.etSignUp1ConfirmPassword.text.toString().trim()
                    if (confirmPassword.isNotEmpty()) {
                        validatePasswords()
                    }
                }
            }

            R.id.etSignUp1ConfirmPassword -> {
                validatePasswords()
            }

            R.id.countryAutoComplete -> {
                val text = binding.countryAutoComplete.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please select a Country Name")
                } else {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
                }
            }

            R.id.cityAutoComplete -> {
                val text = binding.cityAutoComplete.text.toString().trim()
                if (text.isEmpty()) {
                    view.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
                    (activity as? BaseActivity)?.showToast("Please select a City Name")
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

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etFullNameSignUp1.addTextChangedListener(textWatcher)
        binding.etSignUp1Email.addTextChangedListener(textWatcher)
        binding.etSignUp1Password.addTextChangedListener(textWatcher)
        binding.etSignUp1ConfirmPassword.addTextChangedListener(textWatcher)
        binding.countryAutoComplete.addTextChangedListener(textWatcher)
        binding.cityAutoComplete.addTextChangedListener(textWatcher)
    }

    private fun validatePasswords(): Boolean {
        val password = binding.etSignUp1Password.text.toString().trim()
        val confirmPassword = binding.etSignUp1ConfirmPassword.text.toString().trim()
        if (confirmPassword.isEmpty()) {
            binding.etSignUp1ConfirmPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            return false
        } else if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            binding.etSignUp1Password.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            binding.etSignUp1ConfirmPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            (activity as? BaseActivity)?.showToast("Passwords do not match")
            return false
        } else {
            binding.etSignUp1Password.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
            binding.etSignUp1ConfirmPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_focused)
            return true
        }
    }

    private fun updateButtonState() {
        isValid = validateInputs()
        Log.e("SignupBasicInfoFragment", "updateButtonState:$isValid")
        binding.btnSignUpForm1Continue.isEnabled = isValid
        binding.btnSignUpForm1Continue.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun validateInputs(): Boolean {
        val fullName = binding.etFullNameSignUp1.text.toString().trim()
        val email = binding.etSignUp1Email.text.toString().trim()
        val password = binding.etSignUp1Password.text.toString().trim()
        val confirmPassword = binding.etSignUp1ConfirmPassword.text.toString().trim()
        val country = binding.countryAutoComplete.text.toString().trim()
        val city = binding.cityAutoComplete.text.toString().trim()
        val isEmailValid = if (email.isNotEmpty() && isValidEmail(email)) {
            runBlocking { signupDataViewModel.isEmailAvailable(email) }
        } else {
            false
        }
        return fullName.isNotEmpty() &&
                email.isNotEmpty() &&
                isValidEmail(email) &&
                isEmailValid && // Add this check
                password.isNotEmpty() &&
                password.length >= 6 &&
                confirmPassword.isNotEmpty() &&
                country.isNotEmpty() &&
                city.isNotEmpty() &&
                isTickSelected &&
                password == confirmPassword
    }

    override fun onResume() {
        super.onResume()
        // Re-setup the dropdowns to ensure they're properly initialized
        setupCountryDropdown()
        // Restore country selection with flag
        binding.countryAutoComplete.text?.toString()?.let { selectedCountryName ->
            countries().find { it.name == selectedCountryName }?.let { country ->
                val circularFlag = getCircularDrawable(country.flagResId, 60, 14)
                binding.countryAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
                    circularFlag,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down),
                    null
                )
                val currentCity = binding.cityAutoComplete.text?.toString()
                updateCitiesDropdown(country.cities)
                currentCity?.takeIf { city ->
                    country.cities.any { it == city }
                }?.let { validCity ->
                    binding.cityAutoComplete.setText(validCity, false)
                }
            }
        }
        // Restore tick state
        if (isTickSelected) {
            binding.icTick.setImageResource(R.drawable.ic_tick_selected)
        } else {
            binding.icTick.setImageResource(R.drawable.ic_tick_unselected)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}