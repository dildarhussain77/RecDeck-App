package com.example.recdeckapp.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding

    private lateinit var countDownTimer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white_light)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Access PinView using binding
        val otpPinView = binding.otpPinView

        // Set listener to check if PinView is filled or not
        otpPinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Optional: You can handle this if needed
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // If all boxes are filled, change the line color to blue
                if (charSequence?.length == otpPinView.itemCount) {
                    otpPinView.setLineColor(
                        ContextCompat.getColor(
                            this@OtpActivity,
                            R.color.otp_line_color
                        )
                    )  // Change to Blue
                } else {
                    otpPinView.setLineColor(
                        ContextCompat.getColor(
                            this@OtpActivity,
                            R.color.light_grey
                        )
                    ) // Change back to Grey
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // Optional: Handle after text changed
            }
        })

        // Start the countdown timer
        startCountDownTimer()


        // Resend OTP Button Click
        binding.tvResendClick.setOnClickListener {
            //resetOtpInputs()
            startCountDownTimer()
            //enableOtpInputs(true)
        }
        binding.btnOtpContinue.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startCountDownTimer() {
        // Resend button hidden while countdown is active
        binding.tvResendClick.visibility = View.INVISIBLE

        // Create a countdown timer for 60 seconds with 1-second intervals
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Calculate minutes and seconds
                val seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60

                // Format time in MM:SS format
                val timeLeft = String.format("%02d:%02d", minutes, remainingSeconds)
                binding.tvOtpTimer.text = timeLeft
            }

            override fun onFinish() {
                // When timer finishes, show "Resend OTP"
                binding.tvOtpTimer.text = "00:00"
                binding.tvResendClick.visibility = View.VISIBLE
            }
        }
        countDownTimer.start()
    }

    // Optionally, stop the timer if needed, such as when the OTP is verified or activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

}