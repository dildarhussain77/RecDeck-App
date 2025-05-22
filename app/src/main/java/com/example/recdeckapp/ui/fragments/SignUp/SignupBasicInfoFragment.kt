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
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.recdeckapp.R
import com.example.recdeckapp.data.UserRole
import com.example.recdeckapp.databinding.FragmentSignupBasicInfoBinding
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.viewmodel.SignupViewModel

class SignupBasicInfoFragment : Fragment(R.layout.fragment_signup_basic_info) {

    private var _binding: FragmentSignupBasicInfoBinding? = null
    private val binding get() = _binding!!

    private var isPasswordVisible = false
    private var isTickSelected = false

    private lateinit var signupViewModel: SignupViewModel

    private var selectedRole: UserRole = UserRole.NONE


    // Data class to hold country information
    data class Country(
        val name: String,
        val flagResId: Int, // Resource ID for the flag drawable
        val cities: List<String>
    )

    data class City(val name: String) {
        override fun toString(): String {
            return name
        }
    }


    // Sample data with flags
    private val countries = listOf(
        Country("UAE", R.drawable.uae, listOf("Abu Dhabi", "Dubai", "Sharjah", "Ajman")),
        Country("USA", R.drawable.usa, listOf("New York", "Los Angeles", "Chicago")),
        Country("Pak", R.drawable.pakistan, listOf("Islamabad", "Lahore", "Karachi")),
        Country("India", R.drawable.india, listOf("Mumbai", "Delhi", "Bangalore"))
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBasicInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the shared ViewModel from activity
        signupViewModel = (activity as SignupActivity).signupViewModel

        // Observe the role selected and update local var
        signupViewModel.selectedRole.observe(viewLifecycleOwner, Observer { role ->
            selectedRole = role
            // Now you have the role here, you can customize UI or store this info
            // For now, just a debug log or Toast if you want
            Toast.makeText(requireContext(), "Role selected: $role", Toast.LENGTH_SHORT).show()
        })

        setupCountryDropdown()
        setupCityDropdown()

        // Initially disable the continue button
        binding.btnSignUpForm1Continue.isEnabled = false
        binding.btnSignUpForm1Continue.alpha = 0.5f

        setupFieldFocusListeners()

        setupFieldListeners()  // Set up text listeners to update the button state
        updateButtonState()

        binding.ivBackArrowSignUp1.bringToFront()
        binding.ivBackArrowSignUp1.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Set click listener using binding
        binding.ivTogglePasswordSignUp1.setOnClickListener {
            togglePasswordVisibility()

        }


        // Tick icon toggle
        binding.icTick.setOnClickListener {
            isTickSelected = !isTickSelected
            if (isTickSelected) {
                binding.icTick.setImageResource(R.drawable.ic_tick_selected)
            } else {
                binding.icTick.setImageResource(R.drawable.ic_tick_unselected)
            }
            // Update the button state whenever the tick icon is clicked
            updateButtonState()
        }


        binding.btnSignUpForm1Continue.setOnClickListener {
            (activity as SignupActivity).switchFragment(SignupProfileDetailsFragment())
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etSignUp1Password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivTogglePasswordSignUp1.setImageResource(R.drawable.ic_visibility)
        } else {
            binding.etSignUp1Password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivTogglePasswordSignUp1.setImageResource(R.drawable.ic_visibility_off)
        }
        isPasswordVisible = !isPasswordVisible

        // Safely setting the cursor position
        binding.etSignUp1Password.text?.let {
            binding.etSignUp1Password.setSelection(it.length)
        }
    }

    private fun setupCountryDropdown() {
        val adapter = object : ArrayAdapter<Country>(
            requireContext(),
            R.layout.country_dropdown_item,
            R.id.countryNameTextView,
            countries
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.country_dropdown_item, parent, false)

                val country = getItem(position) ?: return view
                view.findViewById<ImageView>(R.id.flagImageView).setImageResource(country.flagResId)
                view.findViewById<TextView>(R.id.countryNameTextView).text = country.name

                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return getView(position, convertView, parent)
            }
        }



        binding.countryAutoComplete.setAdapter(adapter)


        binding.countryAutoComplete.setOnClickListener {

            binding.countryAutoComplete.showDropDown()  // Force dropdown to show
        }

        binding.countryAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = adapter.getItem(position) ?: return@setOnItemClickListener

            // Set the circular flag in AutoCompleteTextView with margin
            val circularFlag =
                getCircularDrawable(selectedCountry.flagResId, 60, 14)  // Size: 60px, Margin: 8px
            binding.countryAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
                circularFlag,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down),
                null
            )
            binding.countryAutoComplete.setText(selectedCountry.name, false)
            updateCitiesDropdown(selectedCountry.cities)
        }


//        binding.countryAutoComplete.setOnItemClickListener { _, _, position, _ ->
//
//            val selectedCountry = adapter.getItem(position) ?:
//
//
//            return@setOnItemClickListener
//
//
//
//            // Set the circular flag in AutoCompleteTextView with margin
//            val circularFlag = getCircularDrawable(selectedCountry.flagResId, 60, 14)  // Size: 60px, Margin: 8px
//            binding.countryAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
//                circularFlag,
//                null,
//                ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down),
//                null
//            )
//            binding.countryAutoComplete.setText(selectedCountry.name, false)
//            updateCitiesDropdown(selectedCountry.cities)
//        }

    }

    private fun getCircularDrawable(drawableRes: Int, size: Int, margin: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableRes) as? BitmapDrawable ?: return null
        val bitmap = drawable.bitmap

        // Resize the bitmap to the desired size
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false)

        // Create a circular bitmap
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        // Draw the circular bitmap
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        // Wrap the circular bitmap in a drawable and add margin
        val circularDrawable = BitmapDrawable(resources, output)
        val insetDrawable = InsetDrawable(circularDrawable, margin)

        return insetDrawable
    }


    private fun setupCityDropdown() {
        // Custom ArrayAdapter for the city dropdown
        val adapter = object : ArrayAdapter<City>(
            requireContext(),
            R.layout.city_dropdown,  // Your custom layout for city items
            R.id.cityNameTextView,   // TextView ID from the custom layout
            emptyList()              // Initialize with an empty list
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.city_dropdown, parent, false)

                // Get the city from the adapter
                val city = getItem(position) ?: return view
                // Set the city name to the TextView in the custom layout
                view.findViewById<TextView>(R.id.cityNameTextView).text = city.name

                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return getView(position, convertView, parent)
            }
        }

        // Set up the city AutoCompleteTextView with the custom adapter
        binding.cityAutoComplete.apply {
            setAdapter(adapter)
            setDropDownBackgroundResource(R.color.white_light)  // Custom dropdown background
            setOnClickListener {
                showDropDown()
            }
        }
    }

    private fun updateCitiesDropdown(cities: List<String>) {
        // Convert the list of city names to a list of City objects
        val cityObjects = cities.map { City(it) }

        // Update the adapter with the new city list
        val adapter = object : ArrayAdapter<City>(
            requireContext(),
            R.layout.city_dropdown,  // Custom city dropdown layout
            R.id.cityNameTextView,   // TextView ID from the custom layout
            cityObjects
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.city_dropdown, parent, false)

                val city = getItem(position) ?: return view
                view.findViewById<TextView>(R.id.cityNameTextView).text = city.name
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return getView(position, convertView, parent)
            }
        }

        // Set the updated adapter to the AutoCompleteTextView
        binding.cityAutoComplete.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        binding.cityAutoComplete.setText("", false)  // Clear previous selection
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
            field.setOnFocusChangeListener { _, hasFocus ->
                val background = if (hasFocus) R.drawable.bg_edit_text_focused else R.drawable.bg_edit_text
                field.background = ContextCompat.getDrawable(requireContext(), background)
            }
        }

        // Special handling for password toggle to not steal focus
        binding.ivTogglePasswordSignUp1.isFocusable = false
        binding.ivTogglePasswordSignUp1.isFocusableInTouchMode = false
        binding.ivTogglePasswordSignUp1.isClickable = true
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

        // Validate password after focus is lost
        binding.etSignUp1Password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePasswords()
        }
        binding.etSignUp1ConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePasswords()
        }
    }

    private fun validatePasswords(): Boolean {
        val password = binding.etSignUp1Password.text.toString().trim()
        val confirmPassword = binding.etSignUp1ConfirmPassword.text.toString().trim()

        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            binding.etSignUp1Password.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            binding.etSignUp1ConfirmPassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text_error)
            Toast.makeText(requireContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return false
        } else {
            binding.etSignUp1Password.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
            binding.etSignUp1ConfirmPassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_edit_text)
        }
        return true
    }

    private fun updateButtonState() {
        val isValid = validateInputs()
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

        return fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                confirmPassword.isNotEmpty() && country.isNotEmpty() && city.isNotEmpty() && isTickSelected
                && password == confirmPassword
    }

    override fun onResume() {
        super.onResume()
        // Restore country selection with flag
        binding.countryAutoComplete.text?.toString()?.let { selectedCountryName ->
            countries.find { it.name == selectedCountryName }?.let { country ->
                // Restore flag image
                val circularFlag = getCircularDrawable(country.flagResId, 60, 14)
                binding.countryAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
                    circularFlag,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down),
                    null
                )

                // Restore cities AND current city selection
                val currentCity = binding.cityAutoComplete.text?.toString()
                updateCitiesDropdown(country.cities)

                // Re-set the selected city if it exists in the current country's cities
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