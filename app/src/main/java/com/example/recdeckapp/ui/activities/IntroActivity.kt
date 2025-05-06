package com.example.recdeckapp.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.IntroPagerAdapter
import com.example.recdeckapp.databinding.ActivityIntroBinding
import android.widget.LinearLayout


class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var adapter: IntroPagerAdapter
    lateinit var introContent: List<Pair<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.white_light)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        introContent = listOf(
            Pair(
                getString(R.string.string_intro_Fragment1_title),
                getString(R.string.string_intro_Fragment1_desc)
            ), Pair(
                getString(R.string.string_intro_Fragment2_title),
                getString(R.string.string_intro_Fragment2_desc)
            ), Pair(
                getString(R.string.string_intro_Fragment3_title),
                getString(R.string.string_intro_Fragment3_desc)
            )
        )
        updateTitleAndDescription(0)

        adapter = IntroPagerAdapter(this, this)
        binding.viewPager.adapter = adapter

        setupIndicators()
        setCurrentIndicator(0)

        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            finish()
        }

        binding.btnContinue.setOnClickListener {
            if (binding.viewPager.currentItem < adapter.itemCount - 1) {
                binding.viewPager.currentItem += 1
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTitleAndDescription(position)
                setCurrentIndicator(position)

                binding.btnContinue.text =
                    if (position == adapter.itemCount - 1) "Get Started" else "Continue"

                binding.btnSkip.visibility =
                    if (position == adapter.itemCount - 1) View.GONE else View.VISIBLE
            }
        })
    }

    private fun updateTitleAndDescription(position: Int) {
        if (position in introContent.indices) {
            val (title, desc) = introContent[position]
            binding.tvTitle.text = title
            binding.tvDescription.text = desc
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.apply {
                setImageResource(
                    if (i == 0) R.drawable.rect_indicator_selected
                    else R.drawable.rect_indicator
                )
                this.layoutParams = layoutParams
                binding.indicatorContainer.addView(this)
            }
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.indicatorContainer.getChildAt(i) as ImageView
            imageView.setImageResource(
                if (i == index) R.drawable.rect_indicator_selected
                else R.drawable.rect_indicator
            )
        }
    }
}
