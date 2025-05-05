package com.example.recdeckapp.ui.activities


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.IntroPagerAdapter


class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: IntroPagerAdapter
    private lateinit var btnContinue: Button

    private lateinit var indicatorContainer: LinearLayout // Container for custom indicators

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        // Set light status bar (means: dark icons)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewPager = findViewById(R.id.viewPager)
        indicatorContainer = findViewById(R.id.indicatorContainer) // Add this LinearLayout in your XML
        btnContinue = findViewById(R.id.btn_Continue)

        adapter = IntroPagerAdapter(this)
        viewPager.adapter = adapter

        setupIndicators() // Add this line to initialize the indicators
        setCurrentIndicator(0) // Set initial indicator

        val btnSkip = findViewById<Button>(R.id.btnSkip)
        btnSkip.setOnClickListener {
            val intent = Intent(this@IntroActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnContinue.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                // Go to next screen
                viewPager.currentItem += 1
            } else {
                // Last screen → Go to Main/Login
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                if (position == adapter.itemCount - 1) {
                    btnContinue.text = "Get Started"
                } else {
                    btnContinue.text = "Continue"
                }
            }
        })
    }

    fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageResource(
                    if (i == 0) R.drawable.rect_indicator_selected
                    else R.drawable.rect_indicator
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
    }

    fun setCurrentIndicator(index: Int) {
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageResource(R.drawable.rect_indicator_selected)
            } else {
                imageView.setImageResource(R.drawable.rect_indicator)
            }
        }
    }
}

