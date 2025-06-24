package com.example.recdeckapp.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityPitchCreationBinding
import com.example.recdeckapp.ui.fragments.createPitches.SelectCategoryPitchFragment
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.StatusBarUtils
import com.example.recdeckapp.utils.loadInitialFragment
import com.example.recdeckapp.viewmodel.PitchCreationViewModel

class PitchCreationActivity : BaseActivity() {
    private lateinit var binding: ActivityPitchCreationBinding

    //ViewModel shared between all fragments
    lateinit var pitchCreationViewModel: PitchCreationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPitchCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pitchCreationViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(PitchCreationViewModel::class.java)
        StatusBarUtils.setLightStatusBar(this, R.color.white_light)
        setOnClickListener()
        binding.stepIndicator.initializeSteps(4)
        if (savedInstanceState == null) {
            loadInitialFragment(
                binding.PitchCreationFragmentContainer.id,
                SelectCategoryPitchFragment()
            )
        }
    }

    private fun setOnClickListener() {
        binding.ivBackPitchCreate.setOnClickListener {
            handleBackPress()
        }
        binding.tvCreatePitchCancel.setOnClickListener {
            AlertDialogUtils.showCancelDialog(
                this,
                message = "Are you sure you want to cancel \n" +
                        "this process?",
                onYesClicked = {
                    finish()
                },
            )
        }
    }

    fun handleBackPress() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    fun updateStepIndicator(step: Int) {
        binding.stepIndicator.setStep(step)
    }

    // Method to show/hide views in the top bar
    fun updateTopBarForFragment(fragmentNumber: Int) {
        when (fragmentNumber) {
            0 -> { // First Fragment
                binding.tvCreatePitch.visibility = View.VISIBLE
                binding.tvCreatePitchCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvSteps.text = getString(R.string.stringStep1)
            }

            1 -> { // Second Fragment
                binding.tvCreatePitch.visibility = View.VISIBLE
                binding.tvCreatePitchCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvSteps.text = getString(R.string.stringStep2)
            }

            2 -> { // Third Fragment
                binding.tvCreatePitch.visibility = View.VISIBLE
                binding.tvCreatePitchCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvSteps.text = getString(R.string.stringStep3)
            }

            3 -> { // fourth Fragment
                binding.tvCreatePitch.visibility = View.VISIBLE
                binding.tvCreatePitchCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvSteps.text = getString(R.string.stringStep4)
            }

            else -> { // Any other fragment
                binding.tvCreatePitch.visibility = View.GONE
                binding.tvCreatePitchCancel.visibility = View.GONE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
            }
        }
    }

    fun showStepIndicator(show: Boolean) {
        binding.stepIndicator.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this) {
            handleBackPress()
        }
    }
}