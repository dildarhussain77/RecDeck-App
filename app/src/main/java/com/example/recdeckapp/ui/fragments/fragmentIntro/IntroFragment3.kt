package com.example.recdeckapp.ui.fragments.fragmentIntro


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
            profile1ImageRes: Int,
            profile2ImageRes: Int,
            profile3ImageRes: Int,
            title: String,
            desc: String
        ): IntroFragment3 {
            val fragment = IntroFragment3()
            val bundle = Bundle().apply {
                putInt("profile1Image", profile1ImageRes)
                putInt("profile2Image", profile2ImageRes)
                putInt("profile3Image", profile3ImageRes)
                putString("title", title)
                putString("desc", desc)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIntro3Binding.inflate(inflater, container, false)

        arguments?.let {
            binding.profile1Image.setImageResource(it.getInt("profile1Image"))
            binding.profile2Image.setImageResource(it.getInt("profile2Image"))
            binding.profile3Image.setImageResource(it.getInt("profile3Image"))
            binding.title3.text = it.getString("title")
            binding.desc3.text = it.getString("desc")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
