package com.example.recdeckapp.ui.fragments.IntroFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recdeckapp.databinding.FragmentIntro1Binding

class IntroFragment1 : Fragment() {

    private var _binding: FragmentIntro1Binding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(
            eventFrames: Int,
        ): IntroFragment1 {
            val fragment = IntroFragment1()
            val bundle = Bundle().apply {

                putInt("eventFrames", eventFrames)

            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntro1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {

            binding.eventFrames.setImageResource(it.getInt("eventFrames"))

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
