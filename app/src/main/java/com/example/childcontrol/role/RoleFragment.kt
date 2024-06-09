package com.example.childcontrol.role

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentRoleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RoleFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentRoleBinding
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_role, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("MyRoles", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding.parentButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_roleFragment_to_headParentFragment)
            editor.putString("role", "parent")
            editor.apply()
        }
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val RoleViewModelFactory = RoleViewModelFactory(mAuth, database)
        val RoleViewModel = ViewModelProvider(this, RoleViewModelFactory)[RoleViewModel::class.java]
        binding.roleViewModel = RoleViewModel
        RoleViewModel.infoChild.observe(viewLifecycleOwner, Observer {

            if (it != null) {
                this.findNavController()
                    .navigate(RoleFragmentDirections.actionRoleFragmentToHeadChildFragment())
                editor.putString("role", "child")
                editor.apply()
            } else {
                this.findNavController().navigate(
                    R.id.action_roleFragment_to_addChildFragment,
                    bundleOf("role" to "child")
                )
                editor.putString("role", "addChild")
                editor.apply()
            }
        })
        binding.childButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_roleFragment_to_headChildFragment)
        }
        // Inflate the layout for this fragment
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Предотвращаем переход к предыдущему фрагменту
                requireActivity().supportFragmentManager.popBackStack(
                    R.id.authFragment,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}