package com.example.recdeckapp.ui.fragments.IntroFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recdeckapp.databinding.FragmentIntro2Binding

class IntroFragment2 : Fragment() {

    private var _binding: FragmentIntro2Binding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(
            mainImage: Int,
        ): IntroFragment2 {
            val fragment = IntroFragment2()
            val bundle = Bundle().apply {
                putInt("mainImage", mainImage)
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
        _binding = FragmentIntro2Binding.inflate(inflater, container, false)

        arguments?.let {
            binding.profileImage.setImageResource(it.getInt("mainImage"))
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
