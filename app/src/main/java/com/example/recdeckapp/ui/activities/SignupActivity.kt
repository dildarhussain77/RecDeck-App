package com.example.recdeckapp.ui.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivitySignupBinding
import com.example.recdeckapp.ui.fragments.SignUp.UserTypeFragment

class SignupActivity : AppCompatActivity() {

    // View Binding object for activity_signup.xml
    private lateinit var binding: ActivitySignupBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white_light)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Initializing view binding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the first fragment when activity is created
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.signupFragmentContainer.id, UserTypeFragment())
                .commit()
        }

    }

    // Method to switch fragments
    fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.signupFragmentContainer.id, fragment)
            .addToBackStack(null)  // To handle back navigation
            .commit()
    }
}