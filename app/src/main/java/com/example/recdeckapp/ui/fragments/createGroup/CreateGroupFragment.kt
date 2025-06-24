package com.example.recdeckapp.ui.fragments.createGroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.FragmentCreateEventBinding
import com.example.recdeckapp.ui.activities.GroupCreationActivity
import com.example.recdeckapp.utils.SessionManager
import com.example.recdeckapp.utils.switchFragment
import com.example.recdeckapp.viewmodel.GroupCreationViewModel

class CreateGroupFragment : Fragment() {
    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var groupCreationViewModel: GroupCreationViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Initialize binding
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateEventBinding.bind(view)
        groupCreationViewModel = (activity as GroupCreationActivity).groupCreationViewModel
        val activity = requireActivity() as? GroupCreationActivity
        activity?.showStepIndicator(false)
        activity?.updateTopBarForFragment(0)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnCreateGroupContinue.setOnClickListener {
            groupCreationViewModel.creatorUserId = SessionManager.getUserId(requireContext())
            Log.e(
                "GroupCreation",
                "User ID continue ButtonHandling: 48 user id=${groupCreationViewModel.creatorUserId},"
            )
            (activity as FragmentActivity).switchFragment(
                R.id.GroupCreationFragmentContainer,
                SelelctCategoryGroupFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}