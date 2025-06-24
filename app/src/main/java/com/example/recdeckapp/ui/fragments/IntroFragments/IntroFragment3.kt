package com.example.recdeckapp.ui.fragments.IntroFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recdeckapp.databinding.FragmentIntro3Binding

class IntroFragment3 : Fragment() {
    private var _binding: FragmentIntro3Binding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(
            chatFrames: Int,
        ): IntroFragment3 {
            val fragment = IntroFragment3()
            val bundle = Bundle().apply {
                putInt("chatFrames", chatFrames)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntro3Binding.inflate(inflater, container, false)
        arguments?.let {
            binding.chatFrames.setImageResource(it.getInt("chatFrames"))
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
