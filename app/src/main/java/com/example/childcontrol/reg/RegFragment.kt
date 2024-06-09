package com.example.childcontrol.reg

import android.app.ActionBar
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentDialogBinding
import com.example.childcontrol.databinding.FragmentRegBinding
import com.example.childcontrol.db.ChildDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class RegFragment : Fragment() {

    private lateinit var binding: FragmentRegBinding

    private lateinit var dialogBinding: FragmentDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reg, container, false)
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_dialog,
            null,
            false
        )
        dialogBinding.progressText = "Идет регистрация\nпожалуйста подождите"
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val viewModelFactory = RegViewModelFactory(mAuth, database)
        val delay = 500
        val regViewModel = ViewModelProvider(this, viewModelFactory)[RegViewModel::class.java]
        binding.regViewModel = regViewModel
        regViewModel.showProgressDialog.observe(viewLifecycleOwner, Observer {
            if (it == true)
                dialog.show()
            else
                Handler(Looper.getMainLooper()).postDelayed({ dialog.dismiss() }, delay.toLong())
        })
        regViewModel.showErrorMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.regErrorMessage.visibility = View.VISIBLE

            } else {
                this.findNavController()
                    .navigate(RegFragmentDirections.actionRegFragmentToAuthFragment())

            }
        })
        // Inflate the layout for this fragment
        return binding.root

    }
}