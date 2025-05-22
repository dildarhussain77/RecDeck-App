package com.example.recdeckapp.ui.activities

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivitySignupBinding
import com.example.recdeckapp.ui.fragments.SignUp.SignupRoleSelectionFragment
import com.example.recdeckapp.utils.StatusBarUtils
import com.example.recdeckapp.viewmodel.SignupViewModel

class SignupActivity : AppCompatActivity() {

    // View Binding object for activity_signup.xml
    private lateinit var binding: ActivitySignupBinding

    // ViewModel shared between all fragments
    val signupViewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtils.setLightStatusBar(this, R.color.white_light)

        // Initializing view binding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the first fragment when activity is created
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.signupFragmentContainer.id, SignupRoleSelectionFragment()).commit()
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

    // Method to switch fragments
    fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.signupFragmentContainer.id, fragment)
            .addToBackStack(null)  // To handle back navigation
            .commit()
    }
}