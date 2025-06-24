package com.example.recdeckapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityLoginBinding
import com.example.recdeckapp.utils.PasswordToggleHelper
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.utils.StatusBarUtils
import com.example.recdeckapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false
    private lateinit var loginPasswordToggleHelper: PasswordToggleHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtils.setLightStatusBar(this, R.color.white_light)
        setFieldFocusListeners()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnLogin.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()
            // Validate fields
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill all fields")
                return@setOnClickListener
            }
            binding.pgBarLogin.visibility = View.VISIBLE
            // Use ViewModel for login
            val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
            lifecycleScope.launch {
                try {
                    val user = loginViewModel.loginUser(email, password)
                    if (user != null) {
                        // Login successful
                        showToast("Login successful!")
                        SessionManager.saveUser(this@LoginActivity, user)
                        // Save user session or navigate to home
                        val intent = Intent(this@LoginActivity, DashBoardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        showToast("Invalid email or password")
                    }
                } catch (e: Exception) {
                    showToast("Login failed: ${e.message}")
                } finally {
                    binding.pgBarLogin.visibility = View.GONE
                }
            }
            Log.e("Login", "Login continueButtonHandling: 59 enter as: ")
        }
        binding.tvForgotPass.setOnClickListener {
            val intent = Intent(this, OtpActivity::class.java)
            startActivity(intent)
        }
        binding.tvSignupClick.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        loginPasswordToggleHelper =
            PasswordToggleHelper(binding.etLoginPassword, binding.ivTogglePassword)
        // Set click listener using binding
        binding.ivTogglePassword.setOnClickListener {
            loginPasswordToggleHelper.toggle()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setFieldFocusListeners() {
        binding.etLoginEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etLoginEmail.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
                binding.tvEmailError.visibility = View.GONE
            } else {
                val email = binding.etLoginEmail.text.toString().trim()
                if (email.isNotEmpty() && isValidEmail(email)) {
                    binding.etLoginEmail.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
                } else {
                    binding.etLoginEmail.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_edit_text)
                }
            }
        }
        binding.etLoginPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etLoginPassword.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
                binding.tvPasswordError.visibility = View.GONE
            } else {
                val password = binding.etLoginPassword.text.toString().trim()
                if (password.isNotEmpty() && password.length >= 6) {
                    binding.etLoginPassword.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
                } else {
                    binding.etLoginPassword.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_edit_text)
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
        binding.etLoginPassword.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text)
        if (email.isEmpty()) {
            binding.tvEmailError.text = "Email cannot be empty"
            binding.tvEmailError.visibility = View.VISIBLE
            binding.etLoginEmail.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else if (!isValidEmail(email)) {
            binding.tvEmailError.text = "Invalid email format"
            binding.tvEmailError.visibility = View.VISIBLE
            binding.etLoginEmail.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else {
            // If valid, set blue background
            binding.etLoginEmail.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        }
        if (password.isEmpty()) {
            binding.tvPasswordError.text = "Password cannot be empty"
            binding.tvPasswordError.visibility = View.VISIBLE
            binding.etLoginPassword.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else if (password.length < 6) {
            binding.tvPasswordError.text = "Password must be at least 6 characters"
            binding.tvPasswordError.visibility = View.VISIBLE
            binding.etLoginPassword.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else {
            // If valid, set blue background
            binding.etLoginPassword.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        }
        return isValid
    }
}
