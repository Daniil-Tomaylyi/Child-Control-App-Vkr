package com.example.childcontrol.forgotpass

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
import com.example.childcontrol.auth.AuthViewModel
import com.example.childcontrol.auth.AuthViewModelFactory
import com.example.childcontrol.databinding.FragmentForgotPassBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPassFragment : Fragment() {
    private lateinit var binding: FragmentForgotPassBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_pass, container, false)
        val mAuth = FirebaseAuth.getInstance()
        val viewModelFactory = ForgotPassViewModelFactory(mAuth)
        val ForgotPassViewModel =
            ViewModelProvider(this, viewModelFactory)[ForgotPassViewModel::class.java]
        binding.forgotPassViewModel = ForgotPassViewModel
        ForgotPassViewModel.showErrorMessageEvent.observe(viewLifecycleOwner, Observer {
            if (it == true)
                binding.errorMsgForgotPass.visibility = View.VISIBLE
            else
                this.findNavController()
                    .navigate(ForgotPassFragmentDirections.actionForgotPassFragmentToAuthFragment())
        })
        return binding.root
    }
}