package com.example.recdeckapp.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.recdeckapp.R
import com.example.recdeckapp.data.roomDatabase.AppDatabase.AppDatabase
import com.example.recdeckapp.utils.StatusBarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen().apply {
                setKeepOnScreenCondition { false } // Let it proceed immediately
            }
            decideNextScreen()
        } else {
            // Android 11 and below — show custom splash layout
            setContentView(R.layout.activity_splash_screen)
            StatusBarUtils.setLightStatusBar(this, R.color.primary_main)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }, 3000)
        }
    }

    private fun decideNextScreen() {
        val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isFirstTime = sharedPrefs.getBoolean("isFirstTime", true)
        //  Room user check karte hain
        lifecycleScope.launch {
            val userDao = AppDatabase.getDatabase(applicationContext).userDao()
            // Get user from DB
            val currentUser = withContext(Dispatchers.IO) {
                sharedPrefs.getString("loggedInEmail", null)?.let { email ->
                    userDao.getUserByEmail(email)
                }
            }
            when {
                isFirstTime -> {
                    // Mark not first time anymore
                    sharedPrefs.edit().putBoolean("isFirstTime", false).apply()
                    startActivity(Intent(this@SplashScreenActivity, IntroActivity::class.java))
                }

                currentUser != null -> {
                    startActivity(Intent(this@SplashScreenActivity, DashBoardActivity::class.java))
                }

                else -> {
                    startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                }
            }
            finish()
        }
    }
}