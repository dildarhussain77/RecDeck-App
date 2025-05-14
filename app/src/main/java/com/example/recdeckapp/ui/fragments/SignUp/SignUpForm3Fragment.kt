package com.example.recdeckapp.ui.fragments.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.CardAdapterIntrest
import com.example.recdeckapp.dataClass.Cards.CardItemIntrests
import com.example.recdeckapp.databinding.FragmentSignUpForm3Binding
import com.example.recdeckapp.ui.activities.SignupActivity


class SignUpForm3Fragment : Fragment(R.layout.fragment_sign_up_form3) {

    private var _binding: FragmentSignUpForm3Binding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding
        _binding = FragmentSignUpForm3Binding.bind(view)

        binding.ivBackArrowSignUp3.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Sample data
        val itemList = listOf(
            CardItemIntrests(R.drawable.basket_ball, "BasketBall"),
            CardItemIntrests(R.drawable.table_tennis, "Table Tennis"),
            CardItemIntrests(R.drawable.horse_ride, "Horse Ride"),
            CardItemIntrests(R.drawable.squash, "Squash"),
            CardItemIntrests(R.drawable.cricket, "Cricket"),
            CardItemIntrests(R.drawable.hockey, "Hockey"),
            CardItemIntrests(R.drawable.cycling, "Cycling"),
            CardItemIntrests(R.drawable.football, "Football"),
            CardItemIntrests(R.drawable.golf, "Golf"),
            CardItemIntrests(R.drawable.swimming_pool, "Swimming pool"),
        )

        // Setting up RecyclerView with GridLayoutManager
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        binding.recyclerView.adapter = CardAdapterIntrest(itemList)

        binding.btnSignUpForm3Continue.setOnClickListener {
            (activity as SignupActivity).switchFragment(AttachDocsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to avoid memory leaks
        _binding = null
    }
}