package com.example.recdeckapp.ui.BottomSheet

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.recdeckapp.R
import com.example.recdeckapp.data.roomDatabase.entities.PitchCreation.PitchEntity
import com.example.recdeckapp.databinding.PitchesDetailsBottomSheetBinding
import com.example.recdeckapp.viewmodel.PitchCreationViewModel
import com.example.reckdeck.adapter.SliderAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.abs
import kotlin.math.min

class PitchesDetailsBottomSheet : BottomSheetDialogFragment() {
    private var _binding: PitchesDetailsBottomSheetBinding? = null
    private val binding get() = _binding!!

    // for auto scroll
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val autoScrollDelay: Long = 2500 // 3 seconds
    private var autoScrollRunnable: Runnable? = null
    private var selectedPitch: PitchEntity? = null
    private lateinit var pitchCreationViewModel: PitchCreationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitchCreationViewModel =
            ViewModelProvider(requireActivity())[PitchCreationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PitchesDetailsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false //Prevents closing on outside tap/back press
        setOnClickListener()
        //setUpManualScrollAdapter()
        Log.d("PitchDebug", "BottomSheet onViewCreated")
        selectedPitch?.let {
            Log.d("PitchDebug", "Manual bind: ${it.pitchName}")
            bindPitchData(it)
        }
    }

    fun setSelectedPitchManually(pitch: PitchEntity) {
        selectedPitch = pitch
    }

    private fun bindPitchData(pitch: PitchEntity) {
        binding.tvPitchTitle.text = pitch.pitchName
        binding.tvPitchDes.text = pitch.pitchDescription
        binding.tvPitchTiming.text = "${pitch.pitchStartTime} - ${pitch.pitchEndTime}"
        binding.tvHostName.text = pitch.pitchFacilityName
        // If you’re using Glide for image loading:
        Glide.with(requireContext())
            .load(pitch.pitchFacilityImageUrl)
            .placeholder(R.drawable.ic_image)
            .into(binding.ivHost)
        setUpAutoScrollAdapter(pitch.pitchDocPaths)
    }

    private fun setOnClickListener() {
        binding.ivClose.setOnClickListener {
            dismiss() // Properly closes the BottomSheet
        }
        binding.ivBackArrowPitchesDetails.setOnClickListener {
            dismiss() // Properly closes the BottomSheet
        }
    }

    private fun setUpAutoScrollAdapter(imageUris: List<String>) {
        val adapter = SliderAdapter(imageUris)
        binding.viewPager.adapter = adapter
        Log.e("TAG", "setUpAutoScrollAdapter: ${imageUris.size}")
        // Fix: Only set offscreenPageLimit if there are items, and use a reasonable limit
        if (imageUris.isNotEmpty()) {
            binding.viewPager.offscreenPageLimit =
                min(imageUris.size, 3) // Limit to max 3 pages to avoid performance issues
        } else {
            binding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        }
        binding.viewPager.clipToPadding = false
        binding.viewPager.clipChildren = false
        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(20))
            addTransformer { page, position ->
                val scale = 0.75f + (1 - abs(position)) * 0.25f
                page.scaleY = scale
            }
        }
        binding.viewPager.setPageTransformer(compositePageTransformer)
        setupDots(imageUris.size)
        setCurrentDot(0)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentDot(position)
            }
        })
        if (imageUris.size > 1) {
            startAutoScroll()
        }
    }

    private fun startAutoScroll() {
        val itemCount = binding.viewPager.adapter?.itemCount ?: return
        autoScrollRunnable = object : Runnable {
            override fun run() {
                val nextItem = (binding.viewPager.currentItem + 1) % itemCount
                binding.viewPager.setCurrentItem(nextItem, true)
                autoScrollHandler.postDelayed(this, autoScrollDelay)
            }
        }
        autoScrollHandler.postDelayed(autoScrollRunnable!!, autoScrollDelay)
    }

    private fun stopAutoScroll() {
        autoScrollRunnable?.let {
            autoScrollHandler.removeCallbacks(it)
        }
    }

    private fun setupDots(count: Int) {
        binding.dotsContainer.removeAllViews()
        for (i in 0 until count) {
            val dot = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(55, 15).apply {
                    marginStart = 8
                    marginEnd = 8
                }
                background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_unfil_tab
                )
            }
            binding.dotsContainer.addView(dot)
        }
    }

    private fun setCurrentDot(index: Int) {
        for (i in 0 until binding.dotsContainer.childCount) {
            val dot = binding.dotsContainer.getChildAt(i)
            val drawable = if (i == index) R.drawable.ic_fill_tab else R.drawable.ic_unfil_tab
            dot.background = ContextCompat.getDrawable(requireContext(), drawable)
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll() // Important: Stop the handler
        _binding = null
    }
}