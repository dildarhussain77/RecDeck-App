package com.example.recdeckapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ActivityEventCreationBinding
import com.example.recdeckapp.ui.fragments.createEvents.SelectCategoryEventFragment
import com.example.recdeckapp.utils.AlertDialogUtils
import com.example.recdeckapp.utils.StatusBarUtils
import com.example.recdeckapp.utils.loadInitialFragment
import com.example.recdeckapp.viewmodel.EventCreationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventCreationActivity : BaseActivity() {
    private lateinit var binding: ActivityEventCreationBinding

    //ViewModel shared between all fragments
    lateinit var eventCreationViewModel: EventCreationViewModel
    private var isEditing = false
    private var eventIdToEdit = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.stepIndicator.initializeSteps(6)
        // Initialize ViewModel first
        eventCreationViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(EventCreationViewModel::class.java)
        handleIntent()
        setupUI()
    }

    private fun handleIntent() {
        intent?.extras?.let { extras ->
            isEditing = extras.getBoolean("isEditing", false)
            eventIdToEdit = extras.getInt("eventId", -1)
        }
    }

    private fun setupUI() {
        StatusBarUtils.setLightStatusBar(this, R.color.white_light)
        setOnClickListener()
        setupBackPressHandler()
        if (isEditing && eventIdToEdit != -1) {
            loadEventForEditing()
        } else {
            // For new event creation, load fragment immediately
            loadInitialFragment(
                binding.EventCreationFragmentContainer.id, SelectCategoryEventFragment()
            )
        }
    }

    private fun loadEventForEditing() {
        binding.pgBarEventCreation.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Load event data first
                val loaded = withContext(Dispatchers.IO) {
                    eventCreationViewModel.loadEventForEditing(eventIdToEdit)
                }
                Log.d(
                    "EditingCheck",
                    "Activity received loaded interests = ${eventCreationViewModel.selectedInterests.map { it.name }}"
                )
                withContext(Dispatchers.Main) {
                    if (loaded != null) {
                        // pop back stack
                        supportFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                        // Now open the fragment AFTER interests are surely set
                        loadInitialFragment(
                            binding.EventCreationFragmentContainer.id,
                            SelectCategoryEventFragment()
                        )
                    } else {
                        showToast("Failed to load event")
                    }
                }
            } catch (e: Exception) {
                showToast("Error loading event data")
            } finally {
                binding.pgBarEventCreation.visibility = View.GONE
            }
        }
    }

    private fun setOnClickListener() {
        binding.ivBackEventCreate.setOnClickListener {
            handleBackPress()
        }
        binding.tvCreateGroupCancel.setOnClickListener {
            AlertDialogUtils.showCancelDialog(
                this,
                message = "Are you sure you want to cancel \n" + "this process?",
                onYesClicked = {
                    finish()
                },
            )
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
                binding.tvEventDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvEventDetail.text = getString(R.string.stringCreateEvent)
                binding.tvSteps.text = getString(R.string.stringStep1)
            }

            1 -> { // Second Fragment
                binding.tvEventDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvEventDetail.text = getString(R.string.stringCreateEvent)
                binding.tvSteps.text = getString(R.string.stringStep2)
            }

            2 -> { // Third Fragment
                binding.tvEventDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvEventDetail.text = getString(R.string.stringCreateEvent)
                binding.tvSteps.text = getString(R.string.stringStep3)
            }

            3 -> { // fourth Fragment
                binding.tvEventDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvEventDetail.text = getString(R.string.stringCreateEvent)
                binding.tvSteps.text = getString(R.string.stringStep4)
            }

            4 -> { // fifth Fragment
                binding.tvEventDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvEventDetail.text = getString(R.string.stringCreateEvent)
                binding.tvSteps.text = getString(R.string.stringStep5)
            }

            5 -> { // sixth Fragment
                binding.tvEventDetail.visibility = View.VISIBLE
                binding.tvCreateGroupCancel.visibility = View.VISIBLE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
                binding.tvEventDetail.text = getString(R.string.stringCreateEvent)
                binding.tvSteps.text = getString(R.string.stringStep6)
            }

            else -> { // Any other fragment
                binding.tvEventDetail.visibility = View.GONE
                binding.tvCreateGroupCancel.visibility = View.GONE
                binding.tvSteps.visibility = View.VISIBLE
                binding.stepIndicator.visibility = View.VISIBLE
            }
        }
    }

    fun showStepIndicator(show: Boolean) {
        binding.stepIndicator.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun handleBackPress() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            finish()
            Log.e("Event Finish", "setupBackPressHandler: ")
        }
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this) {
            handleBackPress()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear editing state when activity is destroyed
        if (isFinishing) {
            eventCreationViewModel.clearEditingState()
        }
    }
}