package com.example.recdeckapp.ui.fragments.fragmentIntro

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
            mainImage: Int, title: String, desc: String,
        ): IntroFragment1 {
            val fragment = IntroFragment1()
            val bundle = Bundle().apply {
                putInt("mainImage", mainImage)
                putString("title", title)
                putString("desc", desc)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntro1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            binding.centerLogo.setImageResource(it.getInt("mainImage"))
            binding.title1.text = it.getString("title")
            binding.desc1.text = it.getString("desc")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
