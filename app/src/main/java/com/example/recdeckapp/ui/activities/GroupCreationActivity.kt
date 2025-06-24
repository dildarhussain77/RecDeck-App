package com.example.recdeckapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityGroupCreationBinding
import com.example.recdeckapp.ui.fragments.createGroup.CreateGroupFragment
import com.example.recdeckapp.ui.fragments.createGroup.SelelctCategoryGroupFragment
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.StatusBarUtils
import com.example.recdeckapp.utils.loadInitialFragment
import com.example.recdeckapp.viewmodel.GroupCreationViewModel

class GroupCreationActivity : BaseActivity() {
    private lateinit var binding: ActivityGroupCreationBinding

    //ViewModel shared between all fragments
    lateinit var groupCreationViewModel: GroupCreationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.stepIndicator.initializeSteps(3)
        groupCreationViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(GroupCreationViewModel::class.java)
        StatusBarUtils.setLightStatusBar(this, R.color.white_light)
        setOnClickListener()
        setupBackPressHandler()
        val startFromSecond = intent.getBooleanExtra("startFromSecondFragment", false)
        if (savedInstanceState == null) {
            if (startFromSecond) {
                loadInitialFragment(
                    binding.GroupCreationFragmentContainer.id,
                    SelelctCategoryGroupFragment()
                )
            } else {
                loadInitialFragment(
                    binding.GroupCreationFragmentContainer.id,
                    CreateGroupFragment()
                )
            }
        }
    }

    private fun setOnClickListener() {
        binding.ivBackGroupCreate.setOnClickListener {
            handleBackPress()
        }
        binding.tvCreateGroupCancel.setOnClickListener {
            AlertDialogUtils.showCancelDialog(
                this,
                message = "Are you sure you want to cancel \n" +
                        "this process?",
                onYesClicked = {
                    finish()
                    Log.e("Event Finish", "setupBackPressHandler: ")
                },
            )
        }
    }

    fun handleBackPress() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            finish() // Agar pehla fragment hai toh activity finish ho jaaye
        }
    }

    // EventCreationActivity.kt
    fun updateStepIndicator(step: Int) {
        binding.stepIndicator.setStep(step)
    }

    // Method to show/hide views in the top bar
    fun updateTopBarForFragment(fragmentNumber: Int) {
        when (fragmentNumber) {
            0 -> { // First Fragment
                binding.tvGroupDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.GONE
                binding.tvSteps.visibility = View.GONE
                binding.stepIndicator.visibility = View.GONE
            }

            1 -> { // Second Fragment
                binding.tvGroupDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvGroupDetail.text = getString(R.string.stringCreateGroup)
                binding.tvSteps.text = getString(R.string.stringStep1)
            }

            2 -> { // Third Fragment
                binding.tvGroupDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvGroupDetail.text = getString(R.string.stringCreateGroup)
                binding.tvSteps.text = getString(R.string.stringStep2)
            }

            3 -> { // fourth Fragment
                binding.tvGroupDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvGroupDetail.text = getString(R.string.stringCreateGroup)
                binding.tvSteps.text = getString(R.string.stringStep3)
            }

            else -> { // Any other fragment
                binding.tvGroupDetail.visibility = View.GONE
                binding.tvCreateGroupCancel.visibility = View.GONE
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