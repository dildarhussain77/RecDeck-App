package com.example.recdeckapp.ui.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivitySignupBinding
import com.example.recdeckapp.ui.fragments.SignUp.SignupRoleSelectionFragment
import com.example.recdeckapp.utils.StatusBarUtils
import com.example.recdeckapp.utils.loadInitialFragment
import com.example.recdeckapp.viewmodel.SignupDataViewModel

class SignupActivity : BaseActivity() {
    // View Binding object for activity_signup.xml
    private lateinit var binding: ActivitySignupBinding

    // ViewModel shared between all fragments
    lateinit var signupDataViewModel: SignupDataViewModel

    //val signupDataViewModel: SignupDataViewModel by
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setLightStatusBar(this, R.color.white_light)
        // Initializing view binding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        signupDataViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SignupDataViewModel::class.java)
        // Load the first fragment when activity is created
        if (savedInstanceState == null) {
            loadInitialFragment(binding.signupFragmentContainer.id, SignupRoleSelectionFragment())
        }
    }
}