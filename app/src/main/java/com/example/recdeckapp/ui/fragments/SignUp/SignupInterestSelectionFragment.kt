package com.example.recdeckapp.ui.fragments.SignUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recdeckapp.R
import com.example.recdeckapp.adapter.CardAdapterIntrest
import com.example.recdeckapp.data.userRole.UserRole
import com.example.recdeckapp.databinding.FragmentSignupInterestSelectionBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.ui.activities.SignupActivity
import com.example.recdeckapp.utils.BackPressHelper
import com.example.recdeckapp.utils.InterestItemsProvider
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.SignupDataViewModel

class SignupInterestSelectionFragment : Fragment(R.layout.fragment_signup_interest_selection) {

    private var _binding: FragmentSignupInterestSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardAdapter: CardAdapterIntrest
    private lateinit var signupDataViewModel: SignupDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize binding
        _binding = FragmentSignupInterestSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize binding
        _binding = FragmentSignupInterestSelectionBinding.bind(view)

        signupDataViewModel = (activity as SignupActivity).signupDataViewModel


        BackPressHelper.handleBackPress(this, binding.ivBackArrowSignUp3)
        continueButtonHandling()

        // Sample data
        val itemList = InterestItemsProvider.getDefaultInterestItems()

        // Initialize adapter
        cardAdapter = CardAdapterIntrest(
            items = itemList,
            context = requireContext()

        )
        // Optional: Listen for selection changes
        cardAdapter.setOnSelectionChangedListener { selectedItems ->
            signupDataViewModel.selectedInterests = selectedItems
            // Update UI based on selections if needed
            updateSelectionCounter(selectedItems.size)
        }
        // Setting up RecyclerView with GridLayoutManager
        binding.recyclerViewSignup.layoutManager =
            GridLayoutManager(requireContext(), 2) // 2 columns
        binding.recyclerViewSignup.adapter = cardAdapter

    }

    private fun continueButtonHandling() {
        binding.btnSignUpForm3Continue.setOnClickListener {

            val role = signupDataViewModel.selectedRole
            when (role) {
                UserRole.ORGANIZER -> {
                    (activity as FragmentActivity).switchFragment(
                        R.id.signupFragmentContainer,
                        SignupSuccessFragment()
                    )
                }

                UserRole.FACILITY -> {
                    (activity as FragmentActivity).switchFragment(
                        R.id.signupFragmentContainer,
                        SignupDocumentUploadFragment()
                    )
                }

                else -> {
                    (activity as? BaseActivity)?.showToast("No valid role selected!")
                }
            }
            Log.e(
                "SignupProfile",
                "Interest continueButtonHandling: 65 interests:${signupDataViewModel.selectedInterests} ",
            )
        }

    }

    private fun updateSelectionCounter(count: Int) {
        // Optional: Update a counter or text that shows how many items are selected
        binding.btnSignUpForm3Continue.text = if (count > 0) {
            "Continue with $count selected"
        } else {
            "Continue"
        }
    }

    override fun onResume() {
        super.onResume()
        // Restore previously selected interests from ViewModel
        val selectedInterests = signupDataViewModel.selectedInterests.map { it.title }

        cardAdapter.items.forEach { item ->
            item.isSelected = selectedInterests.contains(item.title)
        }

        cardAdapter.notifyDataSetChanged()
        updateSelectionCounter(signupDataViewModel.selectedInterests.size)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding to avoid memory leaks
        _binding = null
    }
}