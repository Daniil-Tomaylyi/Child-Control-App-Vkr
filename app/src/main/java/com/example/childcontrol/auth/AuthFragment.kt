package com.example.childcontrol.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.childcontrol.R
import com.example.childcontrol.databinding.FragmentAuthBinding
import com.example.childcontrol.databinding.FragmentDialogBinding
import com.google.firebase.auth.FirebaseAuth


class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private lateinit var dialogBinding: FragmentDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false)
        binding.buttonAuthCreate.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_authFragment_to_regFragment)
        }
        binding.passRec.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_authFragment_to_forgotPassFragment)
        }
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_dialog,
            null,
            false
        )
        dialogBinding.progressText = "Авторизация\nпожалуйста подождите"
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        val mAuth = FirebaseAuth.getInstance()
        val viewModelFactory = AuthViewModelFactory(mAuth)
        val delay = 500
        val AuthViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        binding.authViewModel = AuthViewModel
        AuthViewModel.showProgressDialog.observe(viewLifecycleOwner, Observer {
            if (it == true)
                dialog.show()
            else
                Handler(Looper.getMainLooper()).postDelayed({ dialog.dismiss() }, delay.toLong())
        })
        AuthViewModel.showErrorMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true)
                binding.errorMsgAuth.visibility = View.VISIBLE
            else
                this.findNavController()
                    .navigate(AuthFragmentDirections.actionAuthFragmentToRoleFragment())
        })
        return binding.root

    }
}