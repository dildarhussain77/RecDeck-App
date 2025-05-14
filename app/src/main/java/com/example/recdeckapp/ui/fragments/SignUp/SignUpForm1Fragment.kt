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
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentSignUpForm1Binding
import com.example.recdeckapp.ui.activities.SignupActivity

class SignUpForm1Fragment : Fragment(R.layout.fragment_sign_up_form1) {

    private var _binding: FragmentSignUpForm1Binding? = null
    private val binding get() = _binding!!

    private var isPasswordVisible = false
    private var isTickSelected = false


    // Data class to hold country information
    data class Country(
        val name: String,
        val flagResId: Int, // Resource ID for the flag drawable
        val cities: List<String>
    )

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
        _binding = FragmentSignUpForm1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCountryDropdown()
        setupCityDropdown()

        // Initially disable the continue button
        binding.btnSignUpForm1Continue.isEnabled = false
        binding.btnSignUpForm1Continue.alpha = 0.5f

        setupFieldListeners()  // Set up text listeners to update the button state

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
            (activity as SignupActivity).switchFragment(SignUpForm2Fragment())
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etSignUp1Password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivTogglePasswordSignUp1.setImageResource(R.drawable.ic_visibility_off)
        } else {
            binding.etSignUp1Password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivTogglePasswordSignUp1.setImageResource(R.drawable.ic_visibility)
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

            // 🌟 Set the circular flag in AutoCompleteTextView with margin
            val circularFlag = getCircularDrawable(selectedCountry.flagResId, 60, 12)  // Size: 60px, Margin: 8px
            binding.countryAutoComplete.setCompoundDrawablesWithIntrinsicBounds(
                circularFlag,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down),
                null
            )
            binding.countryAutoComplete.setText(selectedCountry.name, false)
            updateCitiesDropdown(selectedCountry.cities)
        }
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
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            emptyList<String>()
        )
        binding.cityAutoComplete.setAdapter(adapter)
        binding.cityAutoComplete.setOnClickListener {
            binding.cityAutoComplete.showDropDown()  // Force dropdown to show
        }
    }

    private fun updateCitiesDropdown(cities: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            cities
        )
        binding.cityAutoComplete.setAdapter(adapter)
        binding.cityAutoComplete.setText("", false)
    }

    private fun areAllFieldsFilled(): Boolean {
        val fullName = binding.etFullNameSignUp1.text.toString().trim()
        val email = binding.etSignUp1Email.text.toString().trim()
        val password = binding.etSignUp1Password.text.toString().trim()
        val confirmPassword = binding.etSignUp1ConfirmPassword.text.toString().trim()
        val country = binding.countryAutoComplete.text.toString().trim()
        val city = binding.cityAutoComplete.text.toString().trim()

        // Check if any field is empty
        return fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                confirmPassword.isNotEmpty() && country.isNotEmpty() && city.isNotEmpty() && isTickSelected
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

        binding.etFullNameSignUp1.addTextChangedListener(textWatcher)
        binding.etSignUp1Email.addTextChangedListener(textWatcher)
        binding.etSignUp1Password.addTextChangedListener(textWatcher)
        binding.etSignUp1ConfirmPassword.addTextChangedListener(textWatcher)
        binding.countryAutoComplete.addTextChangedListener(textWatcher)
        binding.cityAutoComplete.addTextChangedListener(textWatcher)
    }

    private fun updateButtonState() {
        if (areAllFieldsFilled()) {
            binding.btnSignUpForm1Continue.isEnabled = true
            binding.btnSignUpForm1Continue.alpha = 1.0f
        } else {
            binding.btnSignUpForm1Continue.isEnabled = false
            binding.btnSignUpForm1Continue.alpha = 0.5f
        }
    }

    override fun onResume() {
        super.onResume()
        // Reinitialize the dropdowns to make them clickable again
        setupCountryDropdown()
        setupCityDropdown()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}