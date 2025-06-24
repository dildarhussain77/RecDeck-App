package com.example.recdeckapp.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ViewStepIndicatorBinding

class StepIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var binding: ViewStepIndicatorBinding =
        ViewStepIndicatorBinding.inflate(LayoutInflater.from(context), this, true)
    private val indicatorContainer = binding.stepContainer
    private var indicators: List<View> = emptyList()

    // Call this to set up desired number of steps
    fun initializeSteps(stepCount: Int) {
        indicatorContainer.removeAllViews()
        indicators = List(stepCount) {
            val stepView = View(context).apply {
                layoutParams = LayoutParams(0, dpToPx(6), 1f) // 6dp height just like your XML
                setBackgroundResource(R.drawable.bg_step_unfilled)
                val margin = resources.getDimensionPixelSize(R.dimen.step_margin)
                (layoutParams as MarginLayoutParams).setMargins(margin, 0, margin, 0)
            }
            indicatorContainer.addView(stepView)
            stepView
        }
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun setStep(step: Int) {
        indicators.forEachIndexed { index, view ->
            val isActive = index < step
            view.setBackgroundResource(
                if (isActive) R.drawable.bg_step_filled else R.drawable.bg_step_unfilled
            )
        }
    }
}