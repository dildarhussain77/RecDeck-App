package com.example.recdeckapp.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.recdeckapp.R
import com.example.recdeckapp.RetrofitClient.LoginRetrofitClient
import com.example.recdeckapp.data.network.LoginRequest
import com.example.recdeckapp.data.network.LoginResponse
import com.example.recdeckapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white_light)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFieldFocusListeners()

        // Set click listener using binding
        binding.ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()

        }

        binding.tvForgotPass.setOnClickListener {
            val intent = Intent(this, OtpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            binding.pg.visibility = View.VISIBLE
            val username = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim() // Fixed this line

            // Validate fields
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(username, password)

            // Retrofit call
            LoginRetrofitClient.api.loginUser(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    // Log the status code for debugging
                    Log.d("LoginResponse", "Status Code: ${response.code()}")
                    binding.pg.visibility = View.GONE

                    when (response.code()) {
                        200 -> {
                            // Success - 200 OK
                            val responseBody = response.body()
                            val accessToken = responseBody?.accessToken
                            if (accessToken != null) {
                                Toast.makeText(this@LoginActivity, "Login Successful: $accessToken", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "Login Successful but accessToken is null", Toast.LENGTH_LONG).show()
                            }
                        }
                        400 -> {
                            // Bad Request - 400
                            Log.e("LoginResponse", "Bad Request: ${response.errorBody()?.string()}")
                            Toast.makeText(this@LoginActivity, "Invalid credentials. Please try again.", Toast.LENGTH_LONG).show()
                        }
                        401 -> {
                            // Unauthorized - 401
                            Log.e("LoginResponse", "Unauthorized: ${response.errorBody()?.string()}")
                            Toast.makeText(this@LoginActivity, "Unauthorized. Check your username and password.", Toast.LENGTH_LONG).show()
                        }
                        500 -> {
                            // Server Error - 500
                            Log.e("LoginResponse", "Server Error: ${response.errorBody()?.string()}")
                            Toast.makeText(this@LoginActivity, "Server is down. Try again later.", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            // Other unexpected status codes
                            Log.e("LoginResponse", "Unexpected Response: ${response.errorBody()?.string()}")
                            Toast.makeText(this@LoginActivity, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    binding.pg.visibility = View.GONE
                    Log.e("LoginResponse", "Network Failure: ${t.message}")
                    Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        }

        binding.tvSignupClick.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }



    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivTogglePassword.setImageResource(R.drawable.ic_visibility)
        } else {
            binding.etLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.ivTogglePassword.setImageResource(R.drawable.ic_visibility_off)
        }
        isPasswordVisible = !isPasswordVisible

        // Safely setting the cursor position
        binding.etLoginPassword.text?.let {
            binding.etLoginPassword.setSelection(it.length)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setFieldFocusListeners() {
        binding.etLoginEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etLoginEmail.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
            } else {
                val email = binding.etLoginEmail.text.toString().trim()
                if (email.isNotEmpty() && isValidEmail(email)) {
                    binding.etLoginEmail.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
                } else {
                    binding.etLoginEmail.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text)
                }
            }
        }

        binding.etLoginPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etLoginPassword.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
            } else {
                val password = binding.etLoginPassword.text.toString().trim()
                if (password.isNotEmpty() && password.length >= 6) {
                    binding.etLoginPassword.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
                } else {
                    binding.etLoginPassword.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text)
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()
        var isValid = true

        // Reset error messages and outlines
        binding.tvEmailError.visibility = View.GONE
        binding.tvPasswordError.visibility = View.GONE
        binding.etLoginEmail.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text)
        binding.etLoginPassword.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text)

        if (email.isEmpty()) {
            binding.tvEmailError.text = "Email cannot be empty"
            binding.tvEmailError.visibility = View.VISIBLE
            binding.etLoginEmail.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else if (!isValidEmail(email)) {
            binding.tvEmailError.text = "Invalid email format"
            binding.tvEmailError.visibility = View.VISIBLE
            binding.etLoginEmail.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else {
            // If valid, set blue background
            binding.etLoginEmail.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        }

        if (password.isEmpty()) {
            binding.tvPasswordError.text = "Password cannot be empty"
            binding.tvPasswordError.visibility = View.VISIBLE
            binding.etLoginPassword.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else if (password.length < 6) {
            binding.tvPasswordError.text = "Password must be at least 6 characters"
            binding.tvPasswordError.visibility = View.VISIBLE
            binding.etLoginPassword.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else {
            // If valid, set blue background
            binding.etLoginPassword.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        }

        return isValid
    }

}