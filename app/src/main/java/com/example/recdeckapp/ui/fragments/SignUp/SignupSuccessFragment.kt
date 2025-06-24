package com.example.recdeckapp.ui.fragments.SignUp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.recdeckapp.R
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.databinding.FragmentSignupSuccessBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.LoginActivity
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.utils.BackPressHelper
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.viewmodel.SignupDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupSuccessFragment : Fragment(R.layout.fragment_signup_success) {
    private var _binding: FragmentSignupSuccessBinding? = null
    private val binding get() = _binding!!
    private lateinit var signupDataViewModel: SignupDataViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupSuccessBinding.bind(view)
        signupDataViewModel = (activity as SignupActivity).signupDataViewModel
        BackPressHelper.handleBackPress(this, binding.ivBackArrowSignUpSuccess)
        continueButtonHandling()
    }

    private fun continueButtonHandling() {
        // Button click listener
        binding.btnSignSucessfulUpContinue.setOnClickListener {
            binding.pgBarSucess.visibility = View.VISIBLE
            binding.btnSignSucessfulUpContinue.isEnabled = false
            signupDataViewModel.insertAllDataToRoom { success ->
                activity?.runOnUiThread {
                    binding.pgBarSucess.visibility = View.GONE
                    if (success) {
                        lifecycleScope.launch {
                            val email = signupDataViewModel.email
                            val userDao = AppDatabase.getDatabase(requireContext()).userDao()
                            val insertedUser = withContext(Dispatchers.IO) {
                                userDao.getUserByEmail(email)
                            }
                            if (insertedUser != null) {
                                // custom SessionManager here
                                SessionManager.saveUser(requireContext(), insertedUser)
                            }
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            startActivity(intent)
                            (activity as? BaseActivity)?.showToast("Sign Up Successful!")
                            requireActivity().finish()
                        }
                    } else {
                        binding.btnSignSucessfulUpContinue.isEnabled = true
                        Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Memory leak avoid karne ke liye binding null kar do
        _binding = null
    }
}
