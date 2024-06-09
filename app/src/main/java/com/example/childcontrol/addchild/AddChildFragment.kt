package com.example.childcontrol.addchild

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.auth.AuthViewModelFactory
import com.example.childcontrol.databinding.FragmentAddChildBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AddChildFragment : Fragment() {
    private lateinit var binding: FragmentAddChildBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_child, container, false)
        val userRole = requireArguments().getString("role")
        sharedPreferences = requireActivity().getSharedPreferences("MyRoles", Context.MODE_PRIVATE)
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val viewModelFactory = AddChildViewModelFactory(mAuth, database)
        val addChildViewModel =
            ViewModelProvider(this, viewModelFactory)[AddChildViewModel::class.java]
        binding.addChildViewModel = addChildViewModel
        addChildViewModel.name.observe(viewLifecycleOwner, Observer { nameChild ->
            if (nameChild.isNullOrBlank()) {
                binding.buttonAddChild.isEnabled = false
            } else {
                addChildViewModel.yearBirth.observe(viewLifecycleOwner, Observer { birthYearChild ->
                    if (!birthYearChild.isNullOrBlank()) {
                        binding.buttonAddChild.isEnabled = true
                    } else {
                        binding.buttonAddChild.isEnabled = false

                    }
                })
            }
        })
        addChildViewModel.isReadyToNavigate.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                if (userRole == "parent") {
                    this.findNavController()
                        .navigate(AddChildFragmentDirections.actionAddChildFragmentToHeadParentFragment())
                } else if (userRole == "child") {
                    this.findNavController()
                        .navigate(AddChildFragmentDirections.actionAddChildFragmentToHeadChildFragment())
                }
            }
        })
        return binding.root
    }
}